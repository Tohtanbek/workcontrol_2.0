let isPromoActivated = false;
let promoCounter = 0;
countTotal();
//расчет total
function countTotal() {
    let subTotalEl = document.querySelector("#cart-subtotal");
    let promoEl = document.querySelector("#cart-promo");
    let totalEl = document.querySelector("#cart-total")
    subTotalEl.textContent = 0.0.toString();
    promoEl.textContent = 0.0.toString();
    totalEl.textContent = 0.0.toString();

    let allPriceEl = document.querySelectorAll("div.product-line-price");
    let subTotal = 0.0;
    allPriceEl.forEach(function (el){
        let price = parseFloat(el.textContent)
        subTotal+=price;
    })
    subTotalEl.textContent = subTotal.toString();
    totalEl.textContent = subTotal.toString();
    promoEl.textContent = 0.00.toString();
}


//________________________________

//Слушатель кнопки remove. extra service удаляет, основной сервис перенаправляет поменять
let removeButtons = document.querySelectorAll(".remove-product");
removeButtons.forEach(function (button){
    button.addEventListener('click',function (event){
        let parentEl = event.target.parentNode;
        if (parentEl.getAttribute("main") === "false") {
            let productEl = parentEl.parentNode;
            productEl.remove();
            countTotal();
            isPromoActivated = false;
        }
        else if (parentEl.getAttribute("main")==="true"){
            window.location.href = "/form/select_service";
        }
    })
})
//_________________________________________________________________
//Логика промокодов
//Слушатель кнопки ввести промокод
let applyPromoButton = document.querySelector("#applyPromo");
applyPromoButton.addEventListener("click",function (){
    //Больше 10 попыток нельзя
    if (promoCounter>10 || isPromoActivated){
        return;
    }
    let promoInput = document.querySelector("#promoInput");
    let promoValue = promoInput.value;
    fetch("/form/check_promo?promo="+promoValue,{
        method: "GET",
        headers: {
            "Content-Type":"application/JSON"
        },
    }).then(response => {
        if (!response.ok){
            let applyPromoLabel = document.querySelector("#promoLabel");
            applyPromoLabel.textContent = "Error loading promo code. PLease try later"
        }
        else {
            response.json().then(object => {
                changePricesWithPromo(object)
            })
        }
    })
})

//функция получает мапу id и скидки по промокоду и начинает менять цену
function changePricesWithPromo(promoCodeMap){
    //Проверяем, что по промокоду что-то нашлось
    if (Object.keys(promoCodeMap).length !== 0){
        //Берем все id продуктов
        let ids = getProductIds();
        //Проверяем, у каких продуктов есть скидка и применяем ее
        for (let key in promoCodeMap){
            if (ids.includes(key)){
                applyPromoForProduct(key,promoCodeMap[key])
            }
        }
        //Обновляем finalTotal за счет скидки
        updateFinalTotal()
        //В конце запрещаем еще раз использовать промокод
        isPromoActivated = true;
    }
    //Если пользователь ввел неправильный промокод
    else {
        promoCounter++;
        let promoInput = document.querySelector("#promoInput");
        promoInput.setCustomValidity('Code does not exist')
        promoInput.reportValidity()
    }
}

//Метод получает все id продуктов на странице
function getProductIds(){
    let productDetailsElements = document.querySelectorAll(".product-details");

    let ids = [];

    productDetailsElements.forEach(function(element) {
        ids.push(element.id);
    });
    return ids;
}

//Применяет скидку к конкретному продукту
function applyPromoForProduct(id, discount){
    let cartPromoEl = document.querySelector("#cart-promo");
    let prevCartPromoFloat = parseFloat(cartPromoEl.textContent)
    console.log("prevCartPromoFloat:"+ prevCartPromoFloat)

    let productPriceEl = document.querySelector("#product-line-price-id-"+id)
    let productPrevPrice = parseFloat(productPriceEl.textContent);
    let discountFloat = parseFloat(discount);
    let countedDiscount = (productPrevPrice*discountFloat)/100
    let updatedTotalDiscount = (prevCartPromoFloat + countedDiscount)


    cartPromoEl.textContent = updatedTotalDiscount.toString();
}

//Обновляем финальную сумму за счет скидки
function updateFinalTotal(){
    let cartPromoEl = document.querySelector("#cart-promo");
    let prevCartPromoFloat = parseFloat(cartPromoEl.textContent)

    let finalTotalEl = document.querySelector("#cart-total");
    let finalTotalFloat = parseFloat(finalTotalEl.textContent)

    let updatedFinalTotal = finalTotalFloat-prevCartPromoFloat;

    finalTotalEl.textContent = updatedFinalTotal.toString();
}
//_______________________________________________-

//Логика отправки заказа
let checkoutButton = document.querySelector(".checkout");
checkoutButton.addEventListener("click",function (){
    fetch()
})
