// Set the box shadow for the first image that's selected
var image = document.getElementsByClassName("image-div")[0];
image.style.setProperty("box-shadow", "0 0 6px black");

// Change the image that's displayed when the user mouse overs a different image
function changeImage(image) {
    var images = document.getElementsByClassName("image-div");
    for (i = 0; i < images.length; i++) {
        images[i].style.removeProperty("box-shadow");
    }

    var imageDiv = image.parentNode.parentNode;
    imageDiv.style.setProperty("box-shadow", "0 0 6px black");

    var mainImage = document.getElementById("main-image");
    mainImage.src = image.src;
}

// Open the selected image in a new window
function openImage(image) {
    window.open(image.src, "_blank");
}