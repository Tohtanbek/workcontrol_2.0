let countedPrice;
//Делает уникальный чекбокс, чтобы можно было выбрать только одну услугу
const checkboxes = document.querySelectorAll('input[type="checkbox"]');
checkboxes.forEach(checkbox => {
    checkbox.addEventListener('change', function() {
        if (this.checked) {
            checkboxes.forEach(otherCheckbox => {
                if (otherCheckbox !== this) {
                    otherCheckbox.checked = false;
                    calculatePrice()
                }
            });
        }
        let noneChecked = true;
        checkboxes.forEach(function(checkbox) {
            if (checkbox.checked) {
                noneChecked = false;
            }
        });
        if (noneChecked){
            makeHidden(continueButton);
            makeHidden(displayPrice);
            displayPrice.textContent = "";
        }
    });
});
//_______________

// Расчет минимальной цены или цены в зависимости от введенной площади
let areaInput = document.querySelector("#areaInput");
let displayPrice = document.querySelector("#total-counter");
let continueButton = document.querySelector("#continueButton");
areaInput.addEventListener("input",function (event){
    calculatePrice();
})
function calculatePrice(){
    let noneChecked = true;
    checkboxes.forEach(function(checkbox) {
        if (checkbox.checked) {
            noneChecked = false;
        }
    });
    if (noneChecked){
        makeVisible(displayPrice)
        displayPrice.textContent = "Please choose the service";
        makeHidden(continueButton)
        return;
    }
    let checkedService = document.querySelector('input[type="checkbox"]:checked');
    let price = checkedService.getAttribute("price");
    let minimalPrice = checkedService.getAttribute("minimalPrice");
    let area = areaInput.value;

    if (!area||area.trim()===""){
        makeHidden(continueButton)
        makeHidden(displayPrice)
        displayPrice.textContent = "";
        return
    }

    countedPrice = price*area;
    if (countedPrice<minimalPrice){
        countedPrice = minimalPrice;
    }
    countedPrice = countedPrice.toString();

    if (countedPrice.indexOf(".")) {
        countedPrice = parseFloat(countedPrice)
        countedPrice = countedPrice.toFixed(1);
        countedPrice += '0';
    }
    makeVisible(displayPrice)
    displayPrice.textContent = "Calculated price: "+countedPrice+"$";
    makeVisible(continueButton)
}
//____________________
//Скрываем\показываем элементы с анимацией
function makeVisible(element){
    element.classList.remove("hidden");
    element.classList.add("visible");
}
function makeHidden(element){
    element.classList.add("hidden");
    element.classList.remove("visible");
}
//__________________________________
//Сохраняем данные и перенаправляем дальше
continueButton.addEventListener("click",function (event){
    event.preventDefault();
    fetch("/form/select_service",{
        method: "POST",
        headers: {
            "Content-Type":"application/json"
        },
        body: JSON.stringify(createJson())
    }).then(response => {
        if (!response.ok){
            throw new Error("error submitting")
        } else {
            console.log("redirecting...")
            window.location.href = "/form/select_extra_service";
        }
    })
})

function createJson(){
    let checkedServiceId = document.querySelector('input[type="checkbox"]:checked').id;
    let areaInputValue = areaInput.value;


    return {
        serviceId: checkedServiceId,
        total: countedPrice,
        area: areaInputValue
    }
}