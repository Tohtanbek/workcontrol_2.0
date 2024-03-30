let updatedRows = [];
let workersList;

//Валидатор, запрещающий менять статус после того, как смена закрыта
let noChangeAfterClosed = function(cell, value){
    return cell.getValue()==="Черновик" || cell.getValue()==="Выдано работнику"
}

let assignEquipMenu = createAssignEquipMenu();
let assignEquipTable = createAssignEquipTable()
assignEquipTable.on("tableBuilt",function () {
    assignEquipTable.hideColumn("equipId")
    assignEquipTable.hideColumn("workerId")
    checkIfRedirected();
})



//Отправляем форму и ожидаем ответа, чтобы обновить таблицу, не обновляя всю страницу
document.getElementById("main-form-submit").addEventListener("click",
    async function (event){
        event.preventDefault();
        let form = document.getElementById("main-form");
        const formData = new FormData(form);
        try {
            let response = await fetch('/tables/assignment_equip/add_assignment_equip_row', {
                method: "POST",
                headers: {
                    "Content-Type":"application/json"
                },
                body: JSON.stringify(Object.fromEntries(formData))
            });
            console.log(JSON.stringify(Object.fromEntries(formData)))
            if (!response.ok) {
                throw new Error('Ошибка при отправке формы');
            }
            else {
                console.log("Форма на создание оборудования создана успешно");
                assignEquipTable.setData("/tables/assignment_equip/main_table");
                $('#form-popup').addClass('is-visible');
            }
        } catch (error){
            console.error('Ошибка при отправке формы', error);
        }
    })
//Крестик (закрыть форму)
document.getElementById("form-exit").addEventListener("click",function (){
    closeForm()
})
//-----------------------------------------------------------------------------------


//Таблица выданного оборудования-------------------------------------------------


//Сама таблица оборудования
function createAssignEquipTable() {
    return new Tabulator("#assign-equip-table", {
        ajaxURL: "/tables/assignment_equip/main_table",
        maxHeight: "80%",
        selectableRows: true,
        movableColumns: true,
        addRowPos: "top",
        pagination: "local",
        paginationSize: 10,
        paginationSizeSelector: [10, 20, 30, 50, 100, true],
        paginationCounter: "rows",
        layout: "fitDataTable",
        initialSort:[
            {column:"id", dir:"desc"}, //sort by this first
        ],
        rowContextMenu: assignEquipMenu,
        columns: [
            {title: "Id", field: "id", sorter: "number"},
            {title: "Описание", field: "naming", width: "10%", editor: true},
            {
                title: "Статус", field: "status", editor: "list",
                editorParams: {
                    values:["Черновик", "Выдано работнику", "Отработано",]
                },
                validator:[
                    {
                        type:noChangeAfterClosed,
                    }
                ]
            },
            {title: "Работник", field: "worker"},
            {title: "workerId",field: "workerId"},
            {title: "Оборудование", field: "equipment"},
            {title: "equipId", field: "equipId"},
            {title: "Кол-во", field: "amount", bottomCalc: "sum", bottomCalcParams: {precision: 1}},
            {title: "Сумма", field: "total", bottomCalc: "sum", bottomCalcParams: {precision: 1}},
            {title: "Дата выдачи", field: "startDateTime"},
            {title: "Дата отработки", field: "endDateTime"},
        ]
    });
}

function createAssignEquipMenu(){
    return [
        {
            label: "<i class='fas fa-user'></i>Удалить выбранные ряды",
            action: function (e, row) {
                $('#equip-delete-popup').addClass('is-visible');
            }
        },
    ];
}

//Фильтры для основной таблицы
let filterColumn = document.getElementById("filter-column");
let filterInput = document.getElementById("filter-input");
let filterClearButton = document.getElementById("clear-filter");
filterColumn.addEventListener("change",updateFilter);
filterInput.addEventListener("keyup",updateFilter);

function updateFilter(){
    let filterColumnValue = filterColumn.options[filterColumn.selectedIndex].value;
    assignEquipTable.setFilter(filterColumnValue,"like",filterInput.value);

    filterClearButton.addEventListener("click",function (){
        filterColumn.value = "";
        filterInput.value = "";
        assignEquipTable.clearFilter();
    })
}

//-------------------------------------------------


function closeForm(){
    // let assignEquipForm = document.querySelector(".assign-equip-form");
    // assignEquipForm.style.opacity = "0";
    // setTimeout(function (){assignEquipForm.style.display = "none"},300);//Чтобы была анимация
    // let main = document.querySelector("main");
    // main.style.opacity = "1";
    // main.style.filter = "blur(0px)";
    // document.body.style.overflow = "visible"; //Вернули возможность скролить после нажатия
    // document.getElementById("overlay").style.display = "none";
    window.location.href = "/tables/assignment_equip/main";
}


//При нажатии кнопки коллекция с измененными dto отправляется на сервер и очищается
document.querySelector("#main-table-save-update")
    .addEventListener("click",function () {
    fetch("/tables/assignment_equip/update_assignment_equip_rows",{
        method: "PUT",
        headers:{
            "Content-Type":"application/json"
        },
        body: JSON.stringify(updatedRows)
    }).then(response => {
        if (!response.ok){
            updatedRows = [];
            assignEquipTable.alert("Ошибка. Изменения не сохранены","error");
            setTimeout(function (){
                assignEquipTable.clearAlert();
            },3000)
            throw new Error('DB error')
        }
        else {
            updatedRows = [];
            assignEquipTable.alert("Изменения сохранены успешно");
            setTimeout(function (){
                assignEquipTable.clearAlert();
            },2000)
        }
    })
})

//Слушатель обновления рядов в основной таблице. Сохраняет ряды, в которых внесены изменения
assignEquipTable.on("cellEdited",function (cell){
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

//Функция, проверяющая, перенаправлен ли запрос на страницу со страницы equipment
function checkIfRedirected(){
    let queryParams = new URLSearchParams(window.location.search);
    let isRedirectedParam = queryParams.get("assign_redirect");
    if (isRedirectedParam!=null){
        let equipIdParam = queryParams.get("row_id");
        fetch("/tables/equip/get_equip_by_id", {
            method: "GET",
            headers: {
                "id": equipIdParam
            }
        }).then(response => {
            if (!response.ok){
                assignEquipTable.alert("Ошибка при выдаче оборудования","error")
                setTimeout(function (){
                    assignEquipTable.clearAlert();
                },3000)
                throw new Error("internal server error");
            }
            else {
                //Если успешно загрузили выбранное пользователем оборудование, то показываем форму
                response.json().then(data => loadWorkersList(data,equipIdParam))
            }
        })
    }
}

function loadWorkersList(equipDto,equipId){
    fetch("/tables/worker/main_table",{
        method: "GET"
    }).then(response => {
        if (!response.ok){
            closeForm();
            assignEquipTable.alert("Ошибка сервера при загрузке формы","error");
            setTimeout(function (){
                assignEquipTable.clearAlert();
            },3000)
            throw new Error("internal server error")
        }
        else {
            response.json().then(data => {
                workersList = data
                showForm(equipId,equipDto['naming'],equipDto['amountLeft'])
            })
        }
    })
}


function showForm(equipId,equipName,equipLeft){
    //Вставляем id оборудования, которое выбрал пользователь в передаваемую форму
    console.log(equipId)
    document.getElementById("equipId").value = equipId;

    let assignEquipForm = document.querySelector(".assign-equip-form");
    assignEquipForm.style.display = "block";
    setTimeout(() => {
        assignEquipForm.style.opacity = "1";
    }, 50);
    let main = document.querySelector("main");
    main.style.opacity = "0.5";
    main.style.filter = "blur(5px)";
    document.body.style.overflow = "hidden"; //Убрали возможность скролить после нажатия
    document.getElementById("overlay").style.display = "block";

    //Загружаем работников в форму (но сначала очищаем, чтобы не дублировались):
    let workerSelect = document.querySelector("#workerId");
    while (workerSelect.firstChild) {
        workerSelect.removeChild(workerSelect.firstChild);
    }
    //Добавляем варианты работников
    let freshOption = document.createElement("option");
    freshOption.selected = true;
    freshOption.hidden = true;
    freshOption.value = "default";
    freshOption.text = "Выберите работника";
    workerSelect.appendChild(freshOption);
    console.log(workersList)
    for (let row of workersList){
        let freshOption = document.createElement("option");
        freshOption.value = row['id'];
        freshOption.text = row['name'];
        workerSelect.appendChild(freshOption);
    }

    console.log(equipName)
    console.log(equipLeft)
    //Добавляем подсказки об оборудовании
    let equipNameHintEl = document.getElementById("assignment-equip-name");
    let equipLeftHintEl = document.getElementById("assignment-equip-left");
    equipNameHintEl.textContent = "Выдаем " + equipName;
    equipLeftHintEl.textContent = "Доступно для выдачи на складе: " + equipLeft;
}