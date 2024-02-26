jQuery(document).ready(function($){

    //Закрываем, если не нужна следующая запись
    $('#cd-popup-no').on('click', function(event){
        event.preventDefault();
        $('.cd-popup').removeClass('is-visible');
        closeForm();
    });

    //Очищаем форму, если нужна следующая запись
    $('#cd-popup-yes').on('click', function(event){
        event.preventDefault();
        $('#main-form')[0].reset();
        $('.cd-popup').removeClass('is-visible');
    });

    //close popup when clicking the esc keyboard button
    $(document).keyup(function(event){
        if(event.which==='27'){
            $('.cd-popup').removeClass('is-visible');
        }
    });
});


