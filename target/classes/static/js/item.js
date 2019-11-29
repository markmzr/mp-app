setFirstImageStyle();

function setFirstImageStyle() {
    var firstImage = document.getElementById("first-image");
    firstImage.style.setProperty("border", "1px solid #748aad");
    firstImage.style.setProperty("box-shadow", "0 0 3px #748aad");
}

function changeImage(image) {
    var images = document.getElementsByClassName("image");
    for (i = 0; i < images.length; i++) {
        images[i].style.removeProperty("border");
        images[i].style.removeProperty("box-shadow");
        images[i].style.removeProperty("outline");
        images[i].style.setProperty("border", "1px solid #b7b7b7");
    }
    var imageDiv = image.parentNode.parentNode;
    imageDiv.style.setProperty("border", "1px solid #748aad");
    imageDiv.style.setProperty("box-shadow", "0 0 3px #748aad");

    var mainImage = document.getElementById("selected-image");
    mainImage.src = image.src;
}

function openImage(image) {
    window.open(image.src, "_blank");
}
