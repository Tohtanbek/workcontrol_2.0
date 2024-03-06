jQuery(document).ready(function($){

    //Закрываем, если не удаляем
    $('#expense-popup-no').on('click', function(event){
        event.preventDefault();
        $('.cd-popup').removeClass('is-visible');
    });

    //Удаляем выбранные ряды, если да
    $('#expense-popup-yes').on('click', function(event) {
        event.preventDefault();
        $('.cd-popup').removeClass('is-visible');
        expenseTable.alert("Удаляем записи...", "msg");
        setTimeout(function (){deleteRows()},1000)
    });

    function deleteRows(){
        let deletedIdsArray = [];
        for (selectedRow of expenseTable.getSelectedRows()) {
            deletedIdsArray.push(selectedRow.getData().id)
            selectedRow.delete()
        }
        fetch("/tables/expense/delete_expense_rows", {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(deletedIdsArray)
        })
            .then(response => {
                if (!response.ok) {
                    expenseTable.clearAlert();
                    expenseTable.alert("Ошибка. Изменения не сохранены", "error");
                    setTimeout(function () {
                        expenseTable.clearAlert();
                    }, 4000)
                    throw new Error('DB error')
                } else {
                    expenseTable.setData("/tables/expense/main_table").then(function () {
                        expenseTable.clearAlert();
                        expenseTable.alert("Записи удалены", "msg");
                        setTimeout(function () {
                            expenseTable.clearAlert();
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