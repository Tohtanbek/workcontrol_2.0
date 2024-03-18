let jobsTable;
buildJobsTable();

function buildJobsTable(){
    jobsTable = new Tabulator("#all-jobs-table",{
        data: "/tables/job/main_table",
        layout: "fitDataStretch",
        maxHeight: "80%",
        columns: [
            {title: "Id", field: "id"},
            {title: "Название", field: "name"},
            {title: "Коэффициент ЗП", field: "wageRate"},
            {title: "Коэффициент дохода", field: "incomeRate"},
            {title: "Почасово",field: "isHourly"}
        ]
    });

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

