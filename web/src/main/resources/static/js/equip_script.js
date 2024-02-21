let equipTable;
let typeTable;
let typeMenu = createTypeMenu();
createEquipTable();
createTypeTable();
//Переход к нопке добавить ряд-----------------------------------------------------
//кнопка "добавить ряд" (выводит форму для нового ряда)
document.getElementById("add-row-button").addEventListener("click",function (){
    let equipForm = document.querySelector(".equip-form");
    equipForm.style.display = "block";
    equipForm.style.opacity = "1";
    let main = document.querySelector("main");
    main.style.opacity = "0.5";
    main.style.filter = "blur(5px)";
    document.body.style.overflow = "hidden"; //Убрали возможность скролить после нажатия
    document.getElementById("overlay").style.display = "block";

    //Загружаем типы в форму из таблицы typeTable (но сначала очищаем, чтобы не дублировались):
    let typeSelect = document.querySelector("#type-select");
    while (typeSelect.firstChild) {
        typeSelect.removeChild(typeSelect.firstChild);
    }
    let freshOption = document.createElement("option");
    freshOption.selected = true;
    freshOption.hidden = true;
    freshOption.value = "default";
    freshOption.text = "Выберите тип";
    typeSelect.appendChild(freshOption);
    let typesArray = typeTable.getData();
    for (let row of typesArray){
        let freshOption = document.createElement("option");
        freshOption.value = row['type'];
        freshOption.text = row['type'];
        typeSelect.appendChild(freshOption);
    }
})
//Крестик (закрыть форму)
document.getElementById("form-exit").addEventListener("click",function (){
    let equipForm = document.querySelector(".equip-form");
    equipForm.style.opacity = "0";
    setTimeout(function (){equipForm.style.display = "none"},300);//Чтобы была анимация
    let main = document.querySelector("main");
    main.style.opacity = "1";
    main.style.filter = "blur(0px)";
    document.body.style.overflow = "visible"; //Вернули возможность скролить после нажатия
    document.getElementById("overlay").style.display = "none";
    let typeSelect = document.querySelector("#type-select");
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
    fetch("/equip/add_equip_type",{
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

//editor для колонки типы

//Сама таблица оборудования
function createEquipTable() {
    equipTable = new Tabulator("#equip-table", {
        ajaxURL: "/equip/main_table",
        maxHeight: "80%",
        selectableRows: true,
        movableColumns: true,
        addRowPos: "top",
        pagination: "local",
        paginationSize: 10,
        paginationSizeSelector: [10, 20, 30, 50, 100, true],
        paginationCounter: "rows",
        layout: "fitDataTable",
        columns: [
            {title: "Id", field: "id"},
            {title: "Наименование", field: "naming", width: "10%", editor: true},
            {
                title: "Тип", field: "type", editor: "list", editorParams: {
                    valuesURL: "/equip/equip_types_array"
                }
            },
            {
                title: "Ответственный", field: "responsible", editor: "list", editorParams: {
                    valuesLookup: "active", autocomplete: true, freetext: true
                }
            },
            {title: "Кол-во", field: "amount", editor: "number"},
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




function createTypeTable() {
    typeTable = new Tabulator("#type-table", {
        ajaxURL: "/equip/equip_types",
        layout: "fitDataStretch",
        maxHeight: "80%",
        addRowPos: "top",
        rowContextMenu: typeMenu,
        columns: [
            {
                formatter: "rowSelection", titleFormatter: "rowSelection", hozAlign: "center", headerSort: false,
                cellClick: function (e, cell) {
                    cell.getRow().toggleSelect();
                }
            },
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
//Фильтры для основной таблицы-------------------
let filterColumn = document.getElementById("filter-column");
let filterInput = document.getElementById("filter-input");
let filterClearButton = document.getElementById("clear-filter");
filterColumn.addEventListener("change",updateFilter);
filterInput.addEventListener("keyup",updateFilter);

function updateFilter(){
let filterColumnValue = filterColumn.options[filterColumn.selectedIndex].value;
equipTable.setFilter(filterColumnValue,"like",filterInput.value);

filterClearButton.addEventListener("click",function (){
    filterColumn.value = "";
    filterInput.value = "";
    equipTable.clearFilter();
})
}