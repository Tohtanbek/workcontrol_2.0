let rowPopupFormatter = function (e,row,onRendered){
    let data = row.getData(),
        container = document.createElement("div"),
        contents = "<strong style='font-size:1.2em;'>Бригадиры супервайзера "+data.name+":</strong><br/><ul style='padding:0;  margin-top:10px; margin-bottom:0;'>";
    for (let entry in superBrigadierMap){
        if (entry == data.id) {
            contents += "<li><strong>"+ superBrigadierMap[entry] +"</strong></li>";
        }
    }
    contents += "</ul>";
    container.innerHTML = contents;
    return container;
}

let responsibleTable = createResponsibleTable();
let superBrigadierMap;
responsibleTable.on("tableBuilt",function (){loadLinkedBrigadiers()})

//кнопка "добавить ряд" (выводит форму для нового ряда)
document.getElementById("add-row-button").addEventListener("click",function (){
    let responsibleForm = document.querySelector(".responsible-form");
    responsibleForm.style.display = "block";
    setTimeout(()=>responsibleForm.style.opacity = "1",50);
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
            $('#form-popup').addClass('is-visible');
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
        layout: "fitDataStretch",
        rowContextMenu: createResponsibleMenu(),
        rowDblClickPopup: rowPopupFormatter,
        columns: [
            {title:"Id", field: "id"},
            {title: "Имя", field: "name"},
            {title: "Телефон",field: "phoneNumber",}
        ]
    })
}

function createResponsibleMenu(){
    return [
        {
            label: "<i class='fas fa-user'></i>Удалить выбранные ряды",
            action: function (e, row) {
                $('#supervisor-delete-popup').addClass('is-visible');
            }
        }
    ];
}


//_________________________________________________________________-


function closeForm(){
    let superForm = document.querySelector(".responsible-form");
    superForm.style.opacity = "0";
    setTimeout(function (){superForm.style.display = "none"},300);//Чтобы была анимация
    let main = document.querySelector("main");
    main.style.opacity = "1";
    main.style.filter = "blur(0px)";
    document.body.style.overflow = "visible"; //Вернули возможность скролить после нажатия
    document.getElementById("overlay").style.display = "none";
}

async function loadLinkedBrigadiers() {
    try {
        const response = await fetch("/tables/supervisors/get_supervisor_brigadiers", {
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        });

        if (!response.ok) {
            typeTable.alert("Ошибка. Не удалось загрузить бригадиров", "error");
            setTimeout(function () {
                typeTable.clearAlert();
            }, 2000);
            throw new Error('DB error');
        } else {
            superBrigadierMap = await response.json();
        }
    } catch (error) {
        console.error('Произошла ошибка при загрузке бригадиров для popup:', error);
    }
}
