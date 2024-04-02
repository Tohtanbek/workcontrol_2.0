jQuery(document).ready(function($){

    //Закрываем, если не удаляем
    $('#order-popup-no').on('click', function(event){
        event.preventDefault();
        $('.cd-popup').removeClass('is-visible');
    });

    //Удаляем выбранные ряды, если да
    $('#order-popup-yes').on('click', function(event) {
        event.preventDefault();
        $('.cd-popup').removeClass('is-visible');
        orderTable.alert("Удаляем записи...", "msg");
        setTimeout(function (){deleteRows()},1000)
    });

    function deleteRows(){
        let deletedIdsArray = [];
        for (selectedRow of orderTable.getSelectedRows()) {
            deletedIdsArray.push(selectedRow.getData().id)
            selectedRow.delete()
        }
        fetch("/tables/order/delete_order_rows", {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(deletedIdsArray)
        })
            .then(response => {
                if (!response.ok) {
                    orderTable.clearAlert();
                    orderTable.alert("Ошибка. Изменения не сохранены", "error");
                    setTimeout(function () {
                        orderTable.clearAlert();
                    }, 4000)
                    throw new Error('DB error')
                } else {
                    orderTable.setData("/tables/order/main_table").then(function () {
                        orderTable.clearAlert();
                        orderTable.alert("Записи удалены", "msg");
                        setTimeout(function () {
                            orderTable.clearAlert();
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