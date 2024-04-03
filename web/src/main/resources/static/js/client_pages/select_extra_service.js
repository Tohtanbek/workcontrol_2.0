
let flatPickr = flatpickr("#dateTimePicker", {
    enableTime: true,
    dateFormat: "Y-m-d H:i",
    inline: true
});

flatPickr.config.onChange.push(function (){
    let continueButton = document.querySelector("#continueButton");
    makeVisible(continueButton)
});

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
    fetch("/form/select_extra_service",{
        method: "POST",
        headers: {
            "Content-Type":"application/json",
        },
        body: JSON.stringify(createJson())
    }).then(response => {
        if (!response.ok){
            throw new Error("error submitting")
        } else {
            console.log("redirecting...")
            window.location.href = "/form/cart";
        }
    })
})

function createJson(){
    let checkedElements = document.querySelectorAll('input[type="checkbox"]:checked')
    let checkedServicesIdArray = [];
    for (let element of checkedElements){
        checkedServicesIdArray.push(element.id);
    }
    let dateTimeString = new Date(flatPickr.selectedDates[0]).toString();
    let strippedDateString = dateTimeString.replace(/\s\(.*\)/, '');
    return {
        serviceIds: checkedServicesIdArray,
        dateTime: strippedDateString
    }
}