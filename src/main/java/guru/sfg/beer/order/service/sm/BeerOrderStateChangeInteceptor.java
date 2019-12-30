package guru.sfg.beer.order.service.sm;

import guru.sfg.beer.order.service.domain.BeerOrder;
import guru.sfg.beer.order.service.domain.BeerOrderEventEnum;
import guru.sfg.beer.order.service.domain.BeerOrderStatusEnum;
import guru.sfg.beer.order.service.repositories.BeerOrderRepository;
import guru.sfg.beer.order.service.services.BeerOrderManagerImpl;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BeerOrderStateChangeInteceptor
        extends StateMachineInterceptorAdapter<BeerOrderStatusEnum, BeerOrderEventEnum>
{
    private final BeerOrderRepository beerOrderRepository;

    @Override
    public void preStateChange(State<BeerOrderStatusEnum, BeerOrderEventEnum> state,
                               Message<BeerOrderEventEnum> message,
                               Transition<BeerOrderStatusEnum, BeerOrderEventEnum> transition,
                               StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> stateMachine)
    {
//        Optional.ofNullable(message)
//                .ifPresent(msg ->
//                           {
//                               Optional.ofNullable(UUID.class.cast(msg.getHeaders()
//                                                                      .getOrDefault(BeerOrderServiceImpl.BEER_ORDER_ID_HEADER,
//                                                                                    "")))
//                                       .ifPresent(beerOrderId ->
//                                                  {
//                                                      BeerOrder beerOrder = beerOrderRepository.getOne(beerOrderId);
//                                                      beerOrder.setOrderStatus(state.getId());
//                                                      beerOrderRepository.save(beerOrder);
//                                                  });
//
//                           });
        Optional.ofNullable(message)
                .flatMap(msg -> Optional.ofNullable((String) msg.getHeaders().getOrDefault(
                        BeerOrderManagerImpl.BEER_ORDER_ID_HEADER, " ")))
                .ifPresent(beerOrderId ->
                           {
                               BeerOrder beerOrder = beerOrderRepository.getOne(UUID.fromString(beerOrderId));
                               beerOrder.setOrderStatus(state.getId());
                               beerOrderRepository.saveAndFlush(beerOrder);
                           });
    }
}
