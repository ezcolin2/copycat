$('.message a').click(function(){
   $('form').animate({height: "toggle", opacity: "toggle"}, "slow");
});
setTimeout(function() {
    var errorMessage = document.getElementById('error-message');
    if (errorMessage) {
        errorMessage.style.opacity = '1';
    }
}, 200);

setTimeout(function() {
    var errorMessage = document.getElementById('error-message');
    if (errorMessage) {
        errorMessage.style.opacity = '0';
    }
}, 4000);
setTimeout(function() {
    var errorMessage = document.getElementById('error-message');
    errorMessage.style.display = 'none';
}, 5000);
