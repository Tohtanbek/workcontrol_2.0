let updatedRows = [];

let workerTable = createWorkerTable();

//кнопка "добавить ряд" (выводит форму для нового ряда)
document.getElementById("add-row-button").addEventListener("click",function (){
    let workerForm = document.querySelector(".worker-form");
    workerForm.style.display = "block";
    setTimeout(()=>workerForm.style.opacity = "1",50);
    let main = document.querySelector("main");
    main.style.opacity = "0.5";
    main.style.filter = "blur(5px)";
    document.body.style.overflow = "hidden"; //Убрали возможность скролить после нажатия
    document.getElementById("overlay").style.display = "block";
    //Получаем адреса в форму  (но сначала очищаем, чтобы не дублировались):
    let jsonAddressMap;
    loadAddressJsonMap().then(json => {
        jsonAddressMap = json;
        //Добавляем варианты чекбокса бригадиров (сначала загружаем через api)
        let addressSelectEl = document.querySelector("#addresses");
        for (let entry in jsonAddressMap){
            let freshOption = document.createElement("option");
            freshOption.value = entry;
            freshOption.innerText = jsonAddressMap[entry]
            addressSelectEl.add(freshOption);
        }
    });
    //Получаем профессии в форму
    let jsonJobMap;
    loadJobJsonMap().then(json => {
        console.log(json)
        let jobSelectEl = document.querySelector("#job");
        jsonJobMap = json;
        //Добавляем варианты option
        for (let entry in jsonJobMap){
            let freshVariant = document.createElement("option");
            freshVariant.innerText = jsonJobMap[entry];
            freshVariant.setAttribute("value",entry);
            jobSelectEl.add(freshVariant);
        }
    })

})
//Отправляем форму и ожидаем ответа, чтобы обновить таблицу, не обновляя всю страницу
document.getElementById("main-form-submit").addEventListener("click",
    async function (event){
        event.preventDefault();
        let formJson = JSON.stringify(createFormJson());
        console.log(formJson)
        try {
            let response = await fetch('/tables/worker/add_worker_row', {
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
                workerTable.setData("/tables/worker/main_table");
                $('#form-popup').addClass('is-visible');
            }
        } catch (error){
            console.error('Ошибка при отправке формы', error);
        }
    })
//Крестик (закрыть форму)
document.getElementById("form-exit").addEventListener("click",function (){
    let workerForm = document.querySelector(".worker-form");
    workerForm.style.opacity = "0";
    setTimeout(function (){workerForm.style.display = "none"},300);//Чтобы была анимация
    let main = document.querySelector("main");
    main.style.opacity = "1";
    main.style.filter = "blur(0px)";
    document.body.style.overflow = "visible"; //Вернули возможность скролить после нажатия
    document.getElementById("overlay").style.display = "none";
})
//-----------------------------------------------------------------------------------

//Добавляем таблицы------------------------------------
function createWorkerTable(){
    return new Tabulator("#worker-table",{
        ajaxURL: "/tables/worker/main_table",
        maxHeight: "80%",
        selectableRows: true,
        movableColumns: true,
        addRowPos: "top",
        pagination: "local",
        paginationSize: 10,
        paginationSizeSelector: [10, 20, true],
        paginationCounter: "rows",
        layout: "fitDataStretch",
        rowContextMenu: createWorkerMenu(),
        columns: [
            {title:"Id", field: "id"},
            {title: "Имя", field: "name", editor: true},
            {title: "Телефон",field: "phoneNumber", editor: true},
            {
                title: "Специальность", field: "job", editor: "list",
                editorParams: {
                    valuesURL: "/tables/job/job_names_array"
                }
            },
            {title: "Объекты", field: "addresses"}
        ]
    })
}

function createWorkerMenu(){
    return [
        {
            label: "<i class='fas fa-user'></i>Удалить выбранные ряды",
            action: function (e, row) {
                $('#worker-delete-popup').addClass('is-visible');
            }
        }
    ];
}


//_________________________________________________________________-


function closeForm(){
    let workerForm = document.querySelector(".worker-form");
    workerForm.style.opacity = "0";
    setTimeout(function (){workerForm.style.display = "none"},300);//Чтобы была анимация
    let main = document.querySelector("main");
    main.style.opacity = "1";
    main.style.filter = "blur(0px)";
    document.body.style.overflow = "visible"; //Вернули возможность скролить после нажатия
    document.getElementById("overlay").style.display = "none";
}

// Метод загрузки бригадиров для формы
async function loadAddressJsonMap() {
    try {
        let response = await fetch("/tables/address/load_address_map", {
            method: "GET"
        });
        if (!response.ok) {
            throw new Error("Internal Server Error");
        }
        return await response.json();
    } catch (error) {
        console.error("Error while fetching address map:", error);
        throw error;
    }
}

//Метод загрузки работников в форму
async function loadJobJsonMap() {
    try {
        let response = await fetch("/tables/job/load_job_map", {
            method: "GET"
        });
        if (!response.ok) {
            throw new Error("Internal Server Error");
        }
        return await response.json();
    } catch (error) {
        console.error("Error while fetching job map:", error);
        throw error;
    }
}

//Создает корректный json формы на отправку
function createFormJson(){

    let name = document.querySelector("#name").value;
    let job = document.querySelector("#job").value;
    let phoneNumber = document.querySelector("#phoneNumber").value;
    let selectedAddresses = document.querySelector('#addresses').selectedOptions;

    let selectedAddressesArray = Array.from(selectedAddresses).map(input => input.innerText);

    return {
        name: name,
        jobId: job,
        phoneNumber: phoneNumber,
        addresses: selectedAddressesArray,
    };
}


//При нажатии кнопки коллекция с измененными dto отправляется на сервер и очищается
document.querySelector("#main-table-save-update")
    .addEventListener("click",function () {
        fetch("/tables/worker/update_worker_rows",{
            method: "PUT",
            headers:{
                "Content-Type":"application/json"
            },
            body: JSON.stringify(updatedRows)
        }).then(response => {
            if (!response.ok){
                updatedRows = [];
                workerTable.alert("Ошибка. Изменения не сохранены","error");
                setTimeout(function (){
                    workerTable.clearAlert();
                },3000)
                throw new Error('DB error')
            }
            else {
                updatedRows = [];
                workerTable.alert("Изменения сохранены успешно");
                setTimeout(function (){
                    workerTable.clearAlert();
                },2000)
            }
        })
    })

//Слушатель обновления рядов в основной таблице. Сохраняет ряды, в которых внесены изменения
workerTable.on("cellEdited",function (cell){
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
