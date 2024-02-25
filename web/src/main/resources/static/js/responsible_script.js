let responsibleTable = createResponsibleTable();

//кнопка "добавить ряд" (выводит форму для нового ряда)
document.getElementById("add-row-button").addEventListener("click",function (){
    let responsibleForm = document.querySelector(".responsible-form");
    responsibleForm.style.display = "block";
    responsibleForm.style.opacity = "1";
    let main = document.querySelector("main");
    main.style.opacity = "0.5";
    main.style.filter = "blur(5px)";
    document.body.style.overflow = "hidden"; //Убрали возможность скролить после нажатия
    document.getElementById("overlay").style.display = "block";
})
//Отправляем форму и ожидаем ответа, чтобы обновить таблицу, не обновляя всю страницу
document.getElementById("main-form-submit").addEventListener("click",
    async function (event){
    event.preventDefault();
    let form = document.getElementById("main-form");
    const formData = new FormData(form);
    try {
        let response = await fetch('/tables/supervisors/add_responsible_row', {
            method: "POST",
            headers: {
                "Content-Type":"application/json"
            },
            body: JSON.stringify(Object.fromEntries(formData))
        });
        if (!response.ok) {
            throw new Error('Ошибка при отправке формы');
        }
        else {
            console.log("Форма на создание супервайзера создана успешно");
            responsibleTable.setData("/tables/supervisors/main_table");
        }
    } catch (error){
        console.error('Ошибка при отправке формы', error);
    }
})
//Крестик (закрыть форму)
document.getElementById("form-exit").addEventListener("click",function (){
    let responsibleForm = document.querySelector(".responsible-form");
    responsibleForm.style.opacity = "0";
    setTimeout(function (){responsibleForm.style.display = "none"},300);//Чтобы была анимация
    let main = document.querySelector("main");
    main.style.opacity = "1";
    main.style.filter = "blur(0px)";
    document.body.style.overflow = "visible"; //Вернули возможность скролить после нажатия
    document.getElementById("overlay").style.display = "none";
    let typeSelect = document.querySelector("#type-select");
})
//-----------------------------------------------------------------------------------

//Добавляем таблицы------------------------------------
function createResponsibleTable(){
    return new Tabulator("#responsible-table",{
        ajaxURL: "/tables/supervisors/main_table",
        maxHeight: "80%",
        selectableRows: true,
        movableColumns: true,
        addRowPos: "top",
        pagination: "local",
        paginationSize: 10,
        paginationSizeSelector: [10, 20, true],
        paginationCounter: "rows",
        layout: "fitDataTable",
        columns: [
            {title:"Id", field: "id"},
            {title: "Имя", field: "name"},
            {title: "Телефон",field: "phoneNumber",}
        ]
    })
}
//_________________________________________________________________-