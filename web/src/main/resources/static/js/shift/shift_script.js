let updatedRows = [];

let shiftMenu = createShiftMenu();
let shiftTable = createShiftTable()



//Таблица смен-------------------------------------------------


//Сама таблица смен
function createShiftTable() {
    return new Tabulator("#shift-table", {
        ajaxURL: "/tables/shift/main_table",
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
        rowContextMenu: shiftMenu,
        columns: [
            {title: "Id", field: "id", sorter: "number"},
            {title: "Наименование", field: "naming", width: "10%", editor: true},
            {
                title: "Тип", field: "type", editor: "list", editorParams: {
                    valuesURL: "/tables/shift/shift_types_array"
                }
            },
            {
                title: "Ответственный", field: "responsible", editor: "list", editorParams: {
                    valuesURL: "/tables/supervisors/responsible_names_array",
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

function createShiftMenu(){
    return [
        {
            label: "<i class='fas fa-user'></i>Удалить выбранные ряды",
            action: function (e, row) {
                $('#shift-delete-popup').addClass('is-visible');
            }
        }
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
    shiftTable.setFilter(filterColumnValue,"like",filterInput.value);

    filterClearButton.addEventListener("click",function (){
        filterColumn.value = "";
        filterInput.value = "";
        shiftTable.clearFilter();
    })
}

//-------------------------------------------------


//При нажатии кнопки коллекция с измененными dto отправляется на сервер и очищается
document.querySelector("#main-table-save-update")
    .addEventListener("click",function () {
    fetch("/tables/shift/update_shift_rows",{
        method: "PUT",
        headers:{
            "Content-Type":"application/json"
        },
        body: JSON.stringify(updatedRows)
    }).then(response => {
        if (!response.ok){
            updatedRows = [];
            shiftTable.alert("Ошибка. Изменения не сохранены","error");
            setTimeout(function (){
                shiftTable.clearAlert();
            },3000)
            throw new Error('DB error')
        }
        else {
            updatedRows = [];
            shiftTable.alert("Изменения сохранены успешно");
            setTimeout(function (){
                shiftTable.clearAlert();
            },2000)
        }
    })
})

//Слушатель обновления рядов в основной таблице. Сохраняет ряды, в которых внесены изменения
shiftTable.on("cellEdited",function (cell){
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