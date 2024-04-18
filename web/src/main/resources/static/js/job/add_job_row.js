//кнопка "добавить профессию" (выводит форму для нового ряда)
document.getElementById("add-job-button").addEventListener("click",function () {
    let jobForm = document.querySelector("#main-form");
    jobForm.style.display = "block";
    setTimeout(() => jobForm.style.opacity = "1", 50);
    let main = document.querySelector("main");
    main.style.opacity = "0.5";
    main.style.filter = "blur(5px)";
    document.body.style.overflow = "hidden"; //Убрали возможность скролить после нажатия
    document.getElementById("overlay").style.display = "block";
})

//Отправляем форму и ожидаем ответа, чтобы обновить таблицу, не обновляя всю страницу
document.getElementById("job-form-submit").addEventListener("click",
    async function (event){
        event.preventDefault();
        let formJson = JSON.stringify(createFormJson());
        console.log(formJson)
        try {
            let response = await fetch('/tables/job/add_job_row', {
                method: "POST",
                headers: {
                    "Content-Type":"application/json"
                },
                body: formJson
            });
            if (!response.ok) {
                throw new Error('Ошибка при отправке формы');
            }
            else {
                console.log("Форма на создание профессии создана успешно");
                $('#form-popup').addClass('is-visible');
            }
        } catch (error){
            console.error('Ошибка при отправке формы', error);
        }
    })



function closeForm(){
    let jobForm = document.querySelector("#main-form");
    jobForm.style.opacity = "0";
    setTimeout(function (){jobForm.style.display = "none"},300);//Чтобы была анимация
    let main = document.querySelector("main");
    main.style.opacity = "1";
    main.style.filter = "blur(0px)";
    document.body.style.overflow = "visible"; //Вернули возможность скролить после нажатия
    document.getElementById("overlay").style.display = "none";
}

//Крестик (закрыть форму)
document.getElementById("form-exit").addEventListener("click",function (){
    closeForm()
})

//Создает корректный json формы на отправку
function createFormJson(){

    let name = document.querySelector("#name").value;
    let wageRate = document.querySelector("#wageRate").value;
    let incomeRate  = document.querySelector("#incomeRate").value;
    let isHourly = document.querySelector('#isHourly');
    return {
        name: name,
        wageRate: wageRate,
        incomeRate: incomeRate,
        isHourly: isHourly.checked,
    };
}