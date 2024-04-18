let updatedRows = [];

//Валидатор изменения amount, чтобы запретить ставить меньше оборудования, чем было изначально
let noSubtractAmount = function(cell, value){
    return cell.getValue() < value
}

let typeMenu = createTypeMenu();
let equipMenu = createEquipMenu();
let typeTable = createTypeTable()
let equipTable = createEquipTable()
//Переход к нопке добавить ряд-----------------------------------------------------
//кнопка "добавить ряд" (выводит форму для нового ряда)
document.getElementById("add-row-button").addEventListener("click",async function (){
    let equipForm = document.querySelector("#main-form");
    equipForm.style.display = "block";
    setTimeout(() => {
        equipForm.style.opacity = "1";
    }, 50);
    let main = document.querySelector("main");
    main.style.opacity = "0.5";
    main.style.filter = "blur(5px)";
    document.body.style.overflow = "hidden"; //Убрали возможность скролить после нажатия
    document.getElementById("overlay").style.display = "block";

    //Загружаем типы в форму из таблицы typeTable (но сначала очищаем, чтобы не дублировались):
    let typeSelect = document.querySelector("#type");
    while (typeSelect.firstChild) {
        typeSelect.removeChild(typeSelect.firstChild);
    }
    //Добавляем варианты типов оборудования
    let freshOption = document.createElement("option");
    freshOption.selected = true;
    freshOption.hidden = true;
    freshOption.value = "default";
    freshOption.text = "Выберите тип";
    typeSelect.appendChild(freshOption);
    let typesArray = typeTable.getData();
    console.log(typesArray);
    for (let row of typesArray){
        let freshOption = document.createElement("option");
        freshOption.value = row['name'];
        freshOption.text = row['name'];
        typeSelect.appendChild(freshOption);
    }

    //Добавляем варианты ответственных из другой таблицы
    //Берем их по ссылке в виде массива
    try {
        let response = await fetch("/tables/supervisors/responsible_names_array", {
            method: "GET",
        });
        if (!response.ok) {
            throw new Error("Ошибка при загрузке ответственных при создании формы оборудования")
        } else {
            console.log("Ответственные загружены для выбора в форме оборудования")
            let responsibleSelect = document.querySelector("#responsible");
            while (responsibleSelect.firstChild) {
                responsibleSelect.removeChild(responsibleSelect.firstChild);
            }
            let freshOption = document.createElement("option");
            freshOption.selected = true;
            freshOption.hidden = true;
            freshOption.value = "default";
            freshOption.text = "Выберите ответственного";
            responsibleSelect.appendChild(freshOption);

            let responsibleArray = await response.json();
            for (let row of responsibleArray) {
                let freshOption = document.createElement("option");
                freshOption.value = row;
                freshOption.text = row;
                responsibleSelect.appendChild(freshOption)
            }
        }
    }
    catch (error){
        console.error('Ошибка при загрузке ответственного в форму', error);
    }
})
//Отправляем форму и ожидаем ответа, чтобы обновить таблицу, не обновляя всю страницу
document.getElementById("main-form-submit").addEventListener("click",
    async function (event){
        event.preventDefault();
        let form = document.getElementById("main-form");
        const formData = new FormData(form);
        try {
            let response = await fetch('/tables/equip/add_equip_row', {
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
                equipTable.setData("/tables/equip/main_table");
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

//переход к кнопке "редактировать типы"--------------------------------
document.getElementById("set-equip-types-button").addEventListener("click",function (){
    let typeTable = document.querySelector("#type-table");
    typeTable.style.visibility = "visible";
    typeTable.style.opacity = "1";
    let typeTableDiv = document.querySelector("#type-table-box");
    typeTableDiv.style.opacity = "1";
    typeTableDiv.style.visibility = "visible";
    let main = document.querySelector("main");
    main.style.opacity = "0.5";
    main.style.filter = "blur(5px)";
    document.body.style.overflow = "hidden"; //Убрали возможность скролить после нажатия
    document.getElementById("overlay").style.display = "block";
})

//При нажатии на кнопку "добавить тип"
document.getElementById("add-type-button").addEventListener("click",function (){
    typeTable.addRow({});
})
//Предыдущая стр. типов
document.getElementById("prev-page-type").addEventListener("click",function (){
    typeTable.previousPage();
})
//Следующая стр. типов
document.getElementById("next-page-type").addEventListener("click",function (){
    typeTable.nextPage();
})
//Сохранить изменения в бд
document.getElementById("types-ready-button").addEventListener("click",function (){
    fetch("/tables/equip/add_equip_type",{
        method:'POST',
        headers: {
            'Content-Type': 'application/json' // Указываем тип контента как JSON
        },
        body: JSON.stringify(typeTable.getData())
    })
        .then(response=>{
            if (!response.ok){
                typeTable.alert("Ошибка. Изменения не сохранены","error");
                setTimeout(function (){
                    typeTable.clearAlert();
                },2000)
                throw new Error('DB error')
            }
            else {
                typeTable.setData("/tables/equip/equip_types").then(function () {
                    equipTable.setData("/tables/equip/main_table");
                })
                typeTable.alert("Изменения сохранены")
                setTimeout(function (){
                    typeTable.clearAlert();
                },2000)
            }
        })
})
//Крестик (закрыть типы)
document.getElementById("type-exit").addEventListener("click",function (){
    let typeTable = document.querySelector("#type-table");
    typeTable.style.visibility = "hidden";
    typeTable.style.opacity = "0";
    let typeTableDiv = document.querySelector("#type-table-box");
    typeTableDiv.style.opacity = "0";
    typeTableDiv.style.visibility = "hidden";
    let main = document.querySelector("main");
    main.style.opacity = "1";
    main.style.filter = "blur(0px)";
    document.body.style.overflow = "visible"; //Вернули возможность скролить после нажатия
    document.getElementById("overlay").style.display = "none";
})

//---------------------------------------------------------------------

//Таблица оборудования-------------------------------------------------


//Сама таблица оборудования
function createEquipTable() {
    return new Tabulator("#equip-table", {
        ajaxURL: "/tables/equip/main_table",
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
        rowContextMenu: equipMenu,
        columns: [
            {title: "Id", field: "id", sorter: "number"},
            {title: "Наименование", field: "naming", width: "10%", editor: true},
            {
                title: "Тип", field: "type", editor: "list", editorParams: {
                    valuesURL: "/tables/equip/equip_types_array"
                }
            },
            {title: "Кол-во", field: "amount", editor: "number",validator:[
                    {
                        type:noSubtractAmount,
                    }
                ]},
            {title: "Итого", field: "total", bottomCalc: "sum", bottomCalcParams: {precision: 1}},
            {title: "Цена за 1", field: "price4each"},
            {title: "Остаток суммы", field: "totalLeft"},
            {title: "Остаток кол-во", field: "amountLeft"},
            {
                title: "Ед.изм", field: "unit", editor: "list", editorParams: {
                    valuesLookup: "active", autocomplete: true, freetext: true
                }
            },
            {title: "Выдано кол-во", field: "givenAmount"},
            {title: "Выдано сумма", field: "givenTotal", bottomCalc: "sum", bottomCalcParams: {precision: 1}},
            {title: "Ссылка", field: "link", width: "10%", editor: true},
            {
                title: "Поставщик", field: "source", editor: "list", editorParams: {
                    valuesLookup: "active", autocomplete: true, freetext: true
                }
            },
            {title: "Дата поставки", field: "supplyDate", editor: "date"},
        ]
    });
}


function createEquipMenu(){
    return [
        {
            label: "<i class='fas fa-user'></i>Удалить выбранные ряды",
            action: function (e, row) {
                $('#equip-delete-popup').addClass('is-visible');
            }
        },
        {
            label: "<i class='fas fa-user'></i>Назначить оборудование",
            action: function (e, row) {
                //Перенаправляем на назначение оборудования
                redirectAssignEquip(row)
            }
        }
    ];
}

//redirect на форму выдачи оборудования
function redirectAssignEquip(row){
    let assignRedirectParam = "assign_redirect=true"
    let equipIdParam = row.getCell("id").getValue();
    console.log(equipIdParam);
    window.location.href = "/tables/assignment_equip/main?" + assignRedirectParam + "&row_id=" + equipIdParam;
}

//Фильтры для основной таблицы
let filterColumn = document.getElementById("filter-column");
let filterType = document.getElementById("filter-type");
let filterInput = document.getElementById("filter-input");
let filterClearButton = document.getElementById("clear-filter");
filterColumn.addEventListener("change",updateFilter);
filterType.addEventListener("change",updateFilter);
filterInput.addEventListener("keyup",updateFilter);

function updateFilter(){
    let filterColumnValue = filterColumn.options[filterColumn.selectedIndex].value
    let filterTypeValue = filterType.options[filterType.selectedIndex].value;
    //Если выбрана для фильтра колонка startDate или endDate, то вставляем кастомный фильтр для дат
    if (filterColumnValue === "supplyDate"){
        if (filterTypeValue === "<"){
            equipTable.setFilter(dateTimeFilterLess);
        }
        else if (filterTypeValue === ">"){
            equipTable.setFilter(dateTimeFilterBigger)
        }
        else {
            equipTable.setFilter(filterColumnValue,filterType.value,filterInput.value);
        }
    }else {
        equipTable.setFilter(filterColumnValue,filterType.value,filterInput.value);
    }

}

//Кастомный фильтр для дат
function dateTimeFilterLess(data){
    let filterInput = document.getElementById("filter-input");
    let filterInputValue = filterInput.value;
    let tableStartDateTime = new Date(data.supplyDate);
    let inputStartDateTime = new Date(filterInputValue);
    return tableStartDateTime.getTime()<inputStartDateTime.getTime();
}
//Кастомный фильтр для дат
function dateTimeFilterBigger(data){
    let filterInput = document.getElementById("filter-input");
    let filterInputValue = filterInput.value;
    let tableStartDateTime = new Date(data.supplyDate);
    let inputStartDateTime = new Date(filterInputValue);
    return tableStartDateTime.getTime()>inputStartDateTime.getTime();
}

//Очистка фильтров слушатель
filterClearButton.addEventListener("click",function (){
    filterColumn.value = "";
    filterInput.value = "";
    shiftTable.clearFilter();
})

//-------------------------------------------------
//Таблица типов оборудования
function createTypeTable() {
    return new Tabulator("#type-table", {
        ajaxURL: "/tables/equip/equip_types",
        layout: "fitDataStretch",
        maxHeight: "80%",
        addRowPos: "top",
        rowContextMenu: typeMenu,
        columns: [
            {title: "Id",field: "id"},
            {title: "Тип", field: "name", editor: true}
        ]
    })
}

function createTypeMenu(){
    return [
        {
            label: "<i class='fas fa-user'></i>Удалить ряд",
            action: function (e, row) {
                row.delete();
            }
        },
    ];
}

//------------------------------------------------------------------------------



function closeForm(){
    let equipForm = document.querySelector("#main-form");
    equipForm.style.opacity = "0";
    setTimeout(function (){equipForm.style.display = "none"},300);//Чтобы была анимация
    let main = document.querySelector("main");
    main.style.opacity = "1";
    main.style.filter = "blur(0px)";
    document.body.style.overflow = "visible"; //Вернули возможность скролить после нажатия
    document.getElementById("overlay").style.display = "none";
}


//При нажатии кнопки коллекция с измененными dto отправляется на сервер и очищается
document.querySelector("#main-table-save-update")
    .addEventListener("click",function () {
    fetch("/tables/equip/update_equip_rows",{
        method: "PUT",
        headers:{
            "Content-Type":"application/json"
        },
        body: JSON.stringify(updatedRows)
    }).then(response => {
        if (!response.ok){
            updatedRows = [];
            equipTable.alert("Ошибка. Изменения не сохранены","error");
            setTimeout(function (){
                equipTable.clearAlert();
            },3000)
            throw new Error('DB error')
        }
        else {
            updatedRows = [];
            equipTable.alert("Изменения сохранены успешно");
            setTimeout(function (){
                equipTable.clearAlert();
                equipTable.setData("/tables/equip/main_table");
            },2000)
        }
    })
})

//Слушатель обновления рядов в основной таблице. Сохраняет ряды, в которых внесены изменения
equipTable.on("cellEdited",function (cell){
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