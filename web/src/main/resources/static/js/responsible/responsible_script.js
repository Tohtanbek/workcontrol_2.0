let updatedRows = [];

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
    let responsibleForm = document.querySelector("#main-form");
    responsibleForm.style.display = "block";
    setTimeout(()=>responsibleForm.style.opacity = "1",50);
    let main = document.querySelector("main");
    main.style.opacity = "0.5";
    main.style.filter = "blur(5px)";
    document.body.style.overflow = "hidden"; //Убрали возможность скролить после нажатия
    document.getElementById("overlay").style.display = "block";
    //Получаем бригадиров в форму  (но сначала очищаем, чтобы не дублировались):
    let brigadierCheckBoxDiv = document.querySelector("#brigadiers");
    while (brigadierCheckBoxDiv.firstChild) {
        brigadierCheckBoxDiv.removeChild(brigadierCheckBoxDiv.firstChild);
    }
    let jsonBrigadierMap;
    loadBrigadierJsonMap().then(json => {
        jsonBrigadierMap = json;
        //Добавляем варианты чекбокса бригадиров (сначала загружаем через api)
        for (let entry in jsonBrigadierMap){
            let freshVariant = document.createElement("INPUT");
            freshVariant.setAttribute("type","checkbox");
            freshVariant.setAttribute("name",jsonBrigadierMap[entry]);
            freshVariant.setAttribute("value",entry);
            let label = document.createElement("span");
            label.textContent = jsonBrigadierMap[entry];

            brigadierCheckBoxDiv.appendChild(label);
            brigadierCheckBoxDiv.appendChild(freshVariant)
        }
    });
})
//Отправляем форму и ожидаем ответа, чтобы обновить таблицу, не обновляя всю страницу
document.getElementById("main-form-submit").addEventListener("click",
    async function (event){
        event.preventDefault();
        let formJson = JSON.stringify(createFormJson());
        console.log(formJson)
        try {
            let response = await fetch('/tables/supervisors/add_responsible_row', {
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
                console.log("Форма на создание оборудования создана успешно");
                responsibleTable.setData("/tables/supervisors/main_table");
                $('#form-popup').addClass('is-visible');
            }
        } catch (error){
            console.error('Ошибка при отправке формы', error);
        }
    })
//Крестик (закрыть форму)
document.getElementById("form-exit").addEventListener("click",function (){
    let responsibleForm = document.querySelector("#main-form");
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
            {title: "Имя", field: "name", editor: true},
            {title: "Телефон",field: "phoneNumber", editor: true},
            {title: "Бригадиры",field: "brigadiers"}
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
        },
        {
            label: "<i class='fas fa-user'></i> Изменить список бригадиров",
            action: function (e, row) {
                editBrigadiersOfSupervisorRow(e,row)
            }
        }
    ];
}


//_________________________________________________________________-


function closeForm(){
    let superForm = document.querySelector("#main-form");
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



// Метод загрузки бригадиров для формы
async function loadBrigadierJsonMap() {
    try {
        let response = await fetch("/tables/brigadier/load_brigadier_map", {
            method: "GET"
        });
        if (!response.ok) {
            throw new Error("Internal Server Error");
        }
        return await response.json();
    } catch (error) {
        console.error("Error while fetching brigadier map:", error);
        throw error;
    }
}

//Создает корректный json формы на отправку
function createFormJson(){

    let name = document.querySelector("#name").value;
    let phoneNumber = document.querySelector("#phoneNumber").value;
    let selectedBrigadiers = document.querySelectorAll('#brigadiers input:checked');

    let selectedBrigadiersArray = Array.from(selectedBrigadiers).map(input => input.name);

    return {
        name: name,
        phoneNumber: phoneNumber,
        brigadiers: selectedBrigadiersArray,
    };
}


//При нажатии кнопки коллекция с измененными dto отправляется на сервер и очищается
document.querySelector("#main-table-save-update")
    .addEventListener("click",function () {
        fetch("/tables/supervisors/update_supervisor_rows",{
            method: "PUT",
            headers:{
                "Content-Type":"application/json"
            },
            body: JSON.stringify(updatedRows)
        }).then(response => {
            if (!response.ok){
                updatedRows = [];
                responsibleTable.alert("Ошибка. Изменения не сохранены","error");
                setTimeout(function (){
                    responsibleTable.clearAlert();
                },3000)
                throw new Error('DB error')
            }
            else {
                updatedRows = [];
                responsibleTable.alert("Изменения сохранены успешно");
                setTimeout(function (){
                    responsibleTable.clearAlert();
                },2000)
            }
        })
    })

//Слушатель обновления рядов в основной таблице. Сохраняет ряды, в которых внесены изменения
responsibleTable.on("cellEdited",function (cell){
    let row = cell.getRow().getData();
    console.log(JSON.stringify(row));
    for (let i=0; i<updatedRows.length;i++){
        let prevUpdRow = updatedRows[i];
        if (prevUpdRow.id === row.id){
            updatedRows.splice(i,1);
            break;
        }
    }
    updatedRows.push(row);
    console.log(updatedRows);
    console.log(JSON.stringify(updatedRows));
})


