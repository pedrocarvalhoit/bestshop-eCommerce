$(document).ready(function() {
    $("#logoutLink").on("click", function(e) {
        e.preventDefault();
        document.logoutForm.submit();
    });
});

$(document).ready(function(){
    $("#buttonCancel").on("click", function(){
        window.location = "[[@{/users}]]";
    });

    $("#fileImage").change(function() {
        fileSize = this.files[0].size;
        if(fileSize > 102400){
            this.setCustomValidity("You must choose an image less than 100KB!");
            this.reportValidity();
        }else{
            this.setCustomValidity("");
            showImageThumbnail(this);
        }


    });
});

function showImageThumbnail(fileInput){
    var file = fileInput.files[0];
    var reader = new FileReader();
    reader.onload = function(e) {
        $("#thumbnail").attr("src", e.target.result);
    };
    reader.readAsDataURL(file);
}