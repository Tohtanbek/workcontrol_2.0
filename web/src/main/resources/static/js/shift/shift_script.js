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
            {title: "Отчет", field: "shortInfo", editor: true},
            {title: "Статус", field: "status"},
            {title: "Зона", field: "zone"},
            {title: "Объект", field: "address"},
            {title: "Работник", field: "worker"},
            {title: "Специальность", field: "job"},
            {title: "Бригадир", field: "brigadier"},
            {title: "Начало", field: "startDateTime",sorter:"date"},
            {title: "Конец", field: "endDateTime",sorter:"date"},
            {title: "Время работы", field: "totalHours"},
            {title: "Фото", field: "folderLink", formatter:"link",formatterParams:{
                    label:"Ссылка",
                    target:"_blank"}
            }
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
    if (filterColumnValue === "startDateTime" || filterColumnValue === "endDateTime"){
        if (filterTypeValue === "<"){
            shiftTable.setFilter(dateTimeFilterLess);
        }
        else if (filterTypeValue === ">"){
            shiftTable.setFilter(dateTimeFilterBigger)
        }
        else {
            shiftTable.setFilter(filterColumnValue,filterType.value,filterInput.value);
        }
    }else {
        shiftTable.setFilter(filterColumnValue,filterType.value,filterInput.value);
    }

}

//Кастомный фильтр для дат
function dateTimeFilterLess(data){
    let filterColumnValue = filterColumn.options[filterColumn.selectedIndex].value
    let filterInput = document.getElementById("filter-input");
    let filterInputValue = filterInput.value;
    if (filterColumnValue === "startDateTime") {
        let tableStartDateTime = new Date(data.startDateTime);
        let inputStartDateTime = new Date(filterInputValue);
        return tableStartDateTime.getTime()<inputStartDateTime.getTime();
    }
    else {
        let tableEndDateTime = new Date(data.endDateTime);
        let inputEndDateTime = new Date(filterInputValue);
        return tableEndDateTime.getTime()<inputEndDateTime.getTime();
    }
}
//Кастомный фильтр для дат
function dateTimeFilterBigger(data){
    let filterColumnValue = filterColumn.options[filterColumn.selectedIndex].value
    let filterInput = document.getElementById("filter-input");
    let filterInputValue = filterInput.value;
    if (filterColumnValue === "startDateTime") {
        let tableStartDateTime = new Date(data.startDateTime);
        let inputStartDateTime = new Date(filterInputValue);
        return tableStartDateTime.getTime()>inputStartDateTime.getTime();
    }
    else {
        let tableEndDateTime = new Date(data.endDateTime);
        let inputEndDateTime = new Date(filterInputValue);
        return tableEndDateTime.getTime()>inputEndDateTime.getTime();
    }
}

//Очистка фильтров слушатель
filterClearButton.addEventListener("click",function (){
    filterColumn.value = "";
    filterInput.value = "";
    shiftTable.clearFilter();
})

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