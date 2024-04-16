package com.tosDev.web.spring.web.controllers.client_pages;

import com.tosDev.web.dto.client_pages.*;
import com.tosDev.web.spring.jpa.entity.client_orders.Order;
import com.tosDev.web.spring.web.service.OrderEntityService;
import com.tosDev.web.spring.web.service.ServiceEntityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
@SessionAttributes({"client","mainService","extraServices","cartDto"})
@RequestMapping("/form")
public class CartController {

    private final ServiceEntityService serviceEntityService;
    private final OrderEntityService orderEntityService;
    @GetMapping("/cart")
    String getCartPage(Model model){

        ChosenMainServiceDto mainServiceDto =
                (ChosenMainServiceDto) model.getAttribute("mainService");
        ExtraServiceDateTimeDto extraService =
                (ExtraServiceDateTimeDto) model.getAttribute("extraServices");
        ClientDto clientDto =
                (ClientDto) model.getAttribute("client");
        //Проверка на то, что пользователь прошел алгоритм выбора
        if (mainServiceDto!=null && extraService!=null && clientDto!=null){
            try {
                CartDto cartDto =
                        serviceEntityService
                                .loadDtoForClientCart(extraService.getServiceIds(), mainServiceDto);
                model.addAttribute("cartDto",cartDto);
                return "client_pages/cart";
            } catch (Exception e) {
                log.error("Ошибка при попытке загрузить корзину пользователю {}",
                        clientDto,e);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Ошибка при загрузке услуг");
            }
        }
        else if (clientDto==null){
            return "redirect:authentication";
        }
        else if (mainServiceDto==null){
            return "redirect:select_service";
        }
        else {
            return "redirect:select_extra_service";
        }
    }

    @GetMapping("/check_promo")
    ResponseEntity<Map<Integer,Integer>> checkPromo(@RequestParam("promo") String promo,
                                                    Model model){
        ClientDto client = (ClientDto) model.getAttribute("client");
        try {
            Map<Integer,Integer> discountMap = serviceEntityService.checkPromoCode(promo);
            log.info("Пользователь {} ввел промокод {}, найдено: {}",
                    client,promo,discountMap);
            return ResponseEntity.ok(discountMap);
        } catch (Exception e) {
            log.error("Ошибка при проверке промокода {} пользователем {}",
                    promo,client,e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/checkout")
    ResponseEntity<Void> checkout(@RequestBody CheckoutDto checkoutDto,
                                  Model model){
        ChosenMainServiceDto mainServiceDto =
                (ChosenMainServiceDto) model.getAttribute("mainService");
        ExtraServiceDateTimeDto extraService =
                (ExtraServiceDateTimeDto) model.getAttribute("extraServices");
        ClientDto clientDto =
                (ClientDto) model.getAttribute("client");
        if (mainServiceDto==null || extraService==null || clientDto==null){
            return ResponseEntity.internalServerError().build();
        }
        try {
            Order order = orderEntityService.mapAndSaveOrder(
                    checkoutDto,
                    clientDto,extraService.getDateTime(),mainServiceDto.getArea());
            log.info("Сохранили заказ {} пользователя",order);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Ошибка при сохранении заказа пользователя {}",clientDto,e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/final_page")
    String showFinalPage(SessionStatus sessionStatus){
        //Когда пользователь успешно сделал заказ, очищаем session атрибуты
        sessionStatus.setComplete();
        return "client_pages/final_page";
    }
}
