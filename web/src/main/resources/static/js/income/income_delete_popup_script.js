jQuery(document).ready(function($){

    //Закрываем, если не удаляем
    $('#income-popup-no').on('click', function(event){
        event.preventDefault();
        $('.cd-popup').removeClass('is-visible');
    });

    //Удаляем выбранные ряды, если да
    $('#income-popup-yes').on('click', function(event) {
        event.preventDefault();
        $('.cd-popup').removeClass('is-visible');
        incomeTable.alert("Удаляем записи...", "msg");
        setTimeout(function (){deleteRows()},1000)
    });

    function deleteRows(){
        let deletedIdsArray = [];
        for (selectedRow of incomeTable.getSelectedRows()) {
            deletedIdsArray.push(selectedRow.getData().id)
            selectedRow.delete()
        }
        fetch("/tables/income/delete_income_rows", {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(deletedIdsArray)
        })
            .then(response => {
                if (!response.ok) {
                    incomeTable.clearAlert();
                    incomeTable.alert("Ошибка. Изменения не сохранены", "error");
                    setTimeout(function () {
                        incomeTable.clearAlert();
                    }, 4000)
                    throw new Error('DB error')
                } else {
                    incomeTable.setData("/tables/income/main_table").then(function () {
                        incomeTable.clearAlert();
                        incomeTable.alert("Записи удалены", "msg");
                        setTimeout(function () {
                            incomeTable.clearAlert();
                        }, 2000)
                    })
                }
            });
    }

    //close popup when clicking the esc keyboard button
    $(document).keyup(function(event){
        if(event.which==='27'){
            $('.cd-popup').removeClass('is-visible');
        }
    });
});