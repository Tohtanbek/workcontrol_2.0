jQuery(document).ready(function($){

    //Закрываем, если не удаляем
    $('#job-popup-no').on('click', function(event){
        event.preventDefault();
        $('#job-delete-popup').removeClass('is-visible');
    });

    //Удаляем выбранные ряды, если да
    $('#job-popup-yes').on('click', function(event) {
        event.preventDefault();
        $('#job-delete-popup').removeClass('is-visible');
        jobsTable.alert("Удаляем записи...", "msg");
        setTimeout(function (){deleteRows()},1000)
    });

    function deleteRows(){
        let deletedIdsArray = [];
        for (selectedRow of jobsTable.getSelectedRows()) {
            deletedIdsArray.push(selectedRow.getData().id)
            selectedRow.delete()
        }
        fetch("/tables/job/delete_job_rows", {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(deletedIdsArray)
        })
            .then(response => {
                if (!response.ok) {
                    jobsTable.clearAlert();
                    jobsTable.alert("Ошибка. Изменения не сохранены", "error");
                    setTimeout(function () {
                        jobsTable.clearAlert();
                    }, 4000)
                    throw new Error('DB error')
                } else {
                    jobsTable.setData("/tables/job/main_table").then(function () {
                        jobsTable.clearAlert();
                        jobsTable.alert("Записи удалены", "msg");
                        setTimeout(function () {
                            jobsTable.clearAlert();
                        }, 2000)
                    })
                }
            });
    }

    //close popup when clicking the esc keyboard button
    $(document).keyup(function(event){
        if(event.which==='27'){
            $('#job-delete-popup').removeClass('is-visible');
        }
    });
});