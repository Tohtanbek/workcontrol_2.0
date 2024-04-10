let jobsTable;
let updatedRows = [];
buildJobsTable();

function buildJobsTable(){
    jobsTable = new Tabulator("#all-jobs-table",{
        data: "/tables/job/main_table",
        layout: "fitDataStretch",
        maxHeight: "80%",
        selectableRows: true,
        rowContextMenu: createJobMenu(),
        columns: [
            {title: "Id", field: "id"},
            {title: "Название", field: "name", editor:true},
            {
                title: "Коэфф. ЗП", field: "wageRate", editor: "number",
                editorParams: {
                    min: 0,
                }
            },
            {title: "Коэфф. дохода", field: "incomeRate",
                editor: "number",
                editorParams: {
                    min: 0,
                }
            },
            {title: "Почасово",field: "isHourly", formatter: "tickCross",editor: "tickCross"}
        ]
    });


    function createJobMenu(){
        return [
            {
                label: "<i class='fas fa-user'></i>Удалить выбранные ряды",
                action: function (e, row) {
                    $('#job-delete-popup').addClass('is-visible');
                }
            }
        ];
    }

}
function showJobs() {
    let tableEl = document.querySelector("#all-jobs-table");
    let tableDiv = document.querySelector("#all-jobs-box")
    tableDiv.style.opacity = "1";
    tableDiv.style.visibility = "visible";
    tableEl.style.opacity = "1";
    tableEl.style.visibility = "visible";
    let main = document.querySelector("main");
    main.style.opacity = "0.5";
    main.style.filter = "blur(5px)";
    document.body.style.overflow = "visible"; //Убрали возможность скролить после нажатия
    document.getElementById("overlay").style.display = "block";
}


function closeShowJobs(){
    let jobsTable = document.querySelector("#all-jobs-table");

    jobsTable.style.visibility = "hidden";
    jobsTable.style.opacity = "0";

    let tableDiv = document.querySelector("#all-jobs-box");
    tableDiv.style.opacity = "0";
    tableDiv.style.visibility = "hidden";
    let main = document.querySelector("main");
    main.style.opacity = "1";
    main.style.filter = "blur(0px)";
    document.body.style.overflow = "visible"; //Убрали возможность скролить после нажатия
    document.getElementById("overlay").style.display = "none";
}

//Слушатель обновления рядов в основной таблице. Сохраняет ряды, в которых внесены изменения
jobsTable.on("cellEdited",function (cell){
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

//При нажатии кнопки коллекция с измененными dto отправляется на сервер и очищается
document.querySelector("#save-all-jobs-update")
    .addEventListener("click",function () {
        fetch("/tables/job/update_job_rows",{
            method: "PUT",
            headers:{
                "Content-Type":"application/json"
            },
            body: JSON.stringify(updatedRows)
        }).then(response => {
            if (!response.ok){
                updatedRows = [];
                jobsTable.alert("Ошибка. Изменения не сохранены","error");
                setTimeout(function (){
                    jobsTable.clearAlert();
                },3000)
                throw new Error('DB error')
            }
            else {
                updatedRows = [];
                jobsTable.alert("Изменения сохранены успешно");
                setTimeout(function (){
                    jobsTable.clearAlert();
                },2000)
            }
        })
    })



