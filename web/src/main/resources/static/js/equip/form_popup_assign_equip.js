jQuery(document).ready(function($){

    //Закрываем, если не нужна следующая запись
    $('#cd-popup-no').on('click', function(event){
        event.preventDefault();
        $('.cd-popup').removeClass('is-visible');
        window.location.href = "/tables/assignment_equip/main";
    });

    //Очищаем форму, если нужна следующая запись
    $('#cd-popup-yes').on('click', function(event){
        event.preventDefault();
        $('.cd-popup').removeClass('is-visible');
        window.location.href = "/tables/equip/main";
    });

});
