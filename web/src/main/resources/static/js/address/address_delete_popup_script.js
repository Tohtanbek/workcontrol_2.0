jQuery(document).ready(function($){

    //Закрываем, если не удаляем
    $('#address-popup-no').on('click', function(event){
        event.preventDefault();
        $('.cd-popup').removeClass('is-visible');
    });

    //Удаляем выбранные ряды, если да
    $('#address-popup-yes').on('click', function(event) {
        event.preventDefault();
        $('.cd-popup').removeClass('is-visible');
        addressTable.alert("Удаляем записи...", "msg");
        setTimeout(function (){deleteRows()},1000)
    });

    function deleteRows(){
        let deletedIdsArray = [];
        for (selectedRow of addressTable.getSelectedRows()) {
            deletedIdsArray.push(selectedRow.getData().id)
            selectedRow.delete()
        }
        fetch("/tables/address/delete_address_rows", {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(deletedIdsArray)
        })
            .then(response => {
                if (!response.ok) {
                    addressTable.clearAlert();
                    addressTable.alert("Ошибка. Изменения не сохранены", "error");
                    setTimeout(function () {
                        addressTable.clearAlert();
                    }, 4000)
                    throw new Error('DB error')
                } else {
                    addressTable.setData("/tables/address/main_table").then(function () {
                        addressTable.clearAlert();
                        addressTable.alert("Записи удалены", "msg");
                        setTimeout(function () {
                            addressTable.clearAlert();
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