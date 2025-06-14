const STATUS_OK = 200;
let mediaModal = document.getElementById("mediaModal");
let previewModal = document.getElementById("previewModal");

/* MD Editor */
/* Init */
let simplemde = new SimpleMDE({
    element: document.getElementById("content"),
    forceSync: true,
    toolbar: [
        {
            name: "bold",
            action: SimpleMDE.toggleBold,
            className: "bi bi-type-bold fs-4 mx-1",
            title: "Bold",
        },
        {
            name: "italic",
            action: SimpleMDE.toggleItalic,
            className: "bi bi-type-italic fs-4 mx-1",
            title: "Italic",
        },
        {
            name: "heading-1",
            action: SimpleMDE.toggleHeading1,
            className: "bi bi-type-h1 fs-4 mx-1",
            title: "Heading",
        },
        "|",
        {
            name: "unordered-list",
            action: SimpleMDE.toggleUnorderedList,
            className: "bi bi-list-ul fs-4 mx-1",
            title: "Generic list",
        },
        {
            name: "ordered-list",
            action: SimpleMDE.toggleOrderedList,
            className: "bi bi-list-ol fs-4 mx-1",
            title: "Numbered List",
        },
        {
            name: "table",
            action: SimpleMDE.drawTable,
            className: "bi bi-table fs-4 mx-1",
            title: "Insert Table",
        },
        "|",
        {
            name: "image",
            action: addFileImage,
            className: "bi bi-image fs-4 mx-1",
            title: "Insert Image",
        },
        {
            name: "video",
            action: addVideoLink,
            className: "bi bi-film fs-4 mx-1",
            title: "Insert Video",
        },
        {
            name: "sound",
            action: addFileAudio,
            className: "bi bi-music-note-list fs-4 mx-1",
            title: "Insert Sound",
        },
    ],
});
simplemde.value(document.getElementById("content").value);

/* Adds image form */
function addFileImage() {
    document.getElementById("image").click();
}

/* Adds video form */
function addVideoLink() {
    document.getElementById("video").click();
}

/* Adds audio form */
function addFileAudio() {
    document.getElementById("sound").click();
}

/* Add sound action */
function insertSound() {
    try {
        let selected = document.querySelector(
            'input[name="galleryMedia"]:checked'
        ).value;
        insertMedia("sound", selected);
        document.getElementById("cancelBtn").click();
    } catch (e) {
    }
}

/* Add image action */
function insertImage() {
    try {
        let selected = document.querySelector(
            'input[name="galleryMedia"]:checked'
        ).value;
        insertMedia("image", selected);
        document.getElementById("cancelBtn").click();
    } catch (e) {
    }
}

function attachImage() {
    try {
        let selected = document.querySelector(
            'input[name="galleryMedia"]:checked'
        ).value;
        document.getElementById("preview").value = selected;
        document.getElementById("previewImage").setAttribute("src", `/media/${selected}`);
        document.getElementById("cancelPreviewBtn").click();
    } catch (e) {
    }
}

/* Add sound action */
function insertMedia(mediaType, mediaName) {
    let cm = simplemde.codemirror;
    let output = `<${mediaType}:${mediaName}>`;
    cm.replaceSelection(output);
}

/* Files */

mediaModal.addEventListener("show.bs.modal", fillModal);
previewModal.addEventListener("show.bs.modal", getImages);

async function fillModal(event) {
    let button = event.relatedTarget;
    let mediaType = button.getAttribute("data-bs-whatever");
    document.getElementById("mediaType").value = mediaType.toUpperCase();
    document.getElementById("insertMediaError").innerText = "";
    document
        .getElementById("insertMediaError")
        .setAttribute("hidden", "hidden");
    if (mediaType === "image") {
        document.getElementById("fileField").setAttribute("accept", "image/*");
        document
            .getElementById("attachBtn")
            .setAttribute("onclick", "insertImage();");
    } else if (mediaType === "sound") {
        document.getElementById("fileField").setAttribute("accept", "audio/*");
        document
            .getElementById("attachBtn")
            .setAttribute("onclick", "insertSound();");
    }
    activateSelector(`${mediaType}Selector`);
    await fetchUsersMedia(mediaType);
}

async function getImages(event) {
    document.getElementById("insertPreviewError").innerText = "";
    document
        .getElementById("insertPreviewError")
        .setAttribute("hidden", "hidden");
    document
        .getElementById("attachPreviewBtn")
        .setAttribute("onclick", "attachImage();");
    await fetchUsersMedia("image", "previewGallery");
}

function activateSelector(selectedSelectorID) {
    let imageSelector = document.getElementById("imageSelector");
    let soundSelector = document.getElementById("soundSelector");
    let allSelectors = new Map();
    allSelectors.set("imageSelector", imageSelector);
    allSelectors.set("soundSelector", soundSelector);
    for (let [selectorID, selector] of allSelectors) {
        if (selectorID === selectedSelectorID) {
            selector.setAttribute(
                "class",
                "list-group-item flex-fill px-0 py-2 text-center cursor-default media-item active"
            );
            selector.setAttribute("aria-current", "true");
            selector.setAttribute("onclick", "");
        } else {
            selector.setAttribute(
                "class",
                "list-group-item flex-fill px-0 py-2 text-center cursor-pointer media-item"
            );
            selector.setAttribute("onclick", `switchTo('${selectorID}');`);
        }
    }
}

async function switchTo(selectorID) {
    if (selectorID === "imageSelector") {
        await fillModal({relatedTarget: document.getElementById("image")});
    } else if (selectorID === "soundSelector") {
        await fillModal({relatedTarget: document.getElementById("sound")});
    }
}

async function fetchUsersMedia(mediaType, galleryFieldName = "gallery") {
    let fetchedMedia = await fetch("/media/user-media/" + mediaType, {
        method: "GET",
    });
    fetchedMedia = await fetchedMedia.text();
    fetchedMedia = JSON.parse(fetchedMedia);
    let gallery = document.getElementById(galleryFieldName);
    gallery.innerHTML = "";
    gallery.innerText = "";
    if (mediaType === "image") {
        renderImages(gallery, fetchedMedia);
    } else if (mediaType === "sound") {
        renderSounds(gallery, fetchedMedia);
    } else {
        document.getElementById("fileField").setAttribute("accept", "video/*");
    }
}

function renderImages(gallery, fetchedMedia) {
    for (let i = 0; i < fetchedMedia.length; ++i) {
        gallery.append(
            createGalleryElement(
                i,
                "/media/" + fetchedMedia[i],
                fetchedMedia[i]
            )
        );
    }
    addDisclaimer(gallery, "* Изображения должны иметь размер менее 10 МБ")
}

function renderSounds(gallery, fetchedMedia) {
    for (let i = 0; i < fetchedMedia.length; ++i) {
        gallery.append(
            createGalleryElement(
                i,
                "/static/images/file-earmark-music-fill-big.svg",
                fetchedMedia[i]
            )
        );
    }
    addDisclaimer(gallery, "* Аудио файлы должны иметь размер менее 10 МБ")
}

function addDisclaimer(gallery, message) {
    let disclaimer = document.getElementById("disclaimer");
    disclaimer.innerText = message;
    gallery.parentNode.append(disclaimer);
}

function createGalleryElement(i, iconPath, media) {
    let col = document.createElement("div");
    col.className = "col-sm-6 col-md-4 col-lg-2 m-2";

    let input = document.createElement("input");
    input.className = "btn-check";
    input.setAttribute("type", "radio");
    input.setAttribute("name", "galleryMedia");
    input.setAttribute("autocomplete", "off");
    input.setAttribute("id", "fileIMG" + i);
    input.setAttribute("value", media);

    let label = document.createElement("label");
    label.setAttribute("class", "btn btn-primary w-100");
    label.setAttribute("for", "fileIMG" + i);

    let img = document.createElement("img");
    img.setAttribute("src", iconPath);
    img.className = "img-thumbnail";

    let p = document.createElement("p");
    let filename = media;
    if (filename.length > 16) {
        filename =
            filename.slice(0, 6) + "..." + filename.slice(media.length - 6);
    }
    p.innerText = filename;
    p.setAttribute("class", "mb-0 pb-0 mt-1 mx-0 px-0");

    label.append(img);
    label.append(p);

    col.append(input);
    col.append(label);

    return col;
}

async function insertVideo() {
    let form = document.getElementById("videoLinkForm");
    let data = new URLSearchParams(new FormData(form));
    let resp = await fetch("/media/extract-link", {
        method: "POST",
        body: data,
    });
    resp = await resp.text();
    if (resp.startsWith("Error: ")) {
        document.getElementById("createMediaLinkError").innerText =
            resp.replace("Error: ", "");
        document
            .getElementById("createMediaLinkError")
            .removeAttribute("hidden");
    } else {
        insertMedia("video", resp);
        document.getElementById("videoLinkCloseBtn").click();
        document.getElementById("videoPlayerCode").value = "";
    }
}

/* Upload files on server */
async function postFile() {
    let fileField = document.getElementById("fileField");
    let typeField = document.getElementById("mediaType");
    document.getElementById("insertMediaError").innerText = "";
    document
        .getElementById("insertMediaError")
        .setAttribute("hidden", "hidden");
    if (fileField.files.length !== 0) {
        let data = new FormData();
        data.append("media-file", fileField.files[0]);
        data.append("mediaType", typeField.value);
        let resp = await fetch("/media/upload", {
            method: "POST",
            body: data,
        });
        let status = resp.status
        resp = await resp.text();
        await fetchUsersMedia(typeField.value.toLowerCase());
        if (status !== STATUS_OK) {
            document.getElementById("insertMediaError").innerText =
                "Что-то пошло не так. Попробуйте сжать медиа и повторить попытку."
            document
                .getElementById("insertMediaError")
                .removeAttribute("hidden");
        } else if (resp.startsWith("Error: ")) {
            document.getElementById("insertMediaError").innerText =
                resp.replace("Error: ", "");
            document
                .getElementById("insertMediaError")
                .removeAttribute("hidden");
        } else {
            let files = document
                .getElementById("gallery")
                .getElementsByTagName("input");
            for (let i = 0; i < files.length; ++i) {
                if (files[i].value === resp) {
                    files[i].setAttribute("checked", "checked");
                    break;
                }
            }
        }
        try {
            fileField.value = "";
            if (fileField.value) {
                fileField.type = "text";
                fileField.type = "file";
            }
        } catch (e) {
        }
    }
}

async function sendImage() {
    let fileField = document.getElementById("previewFileField");
    document.getElementById("insertPreviewError").innerText = "";
    document
        .getElementById("insertPreviewError")
        .setAttribute("hidden", "hidden");
    if (fileField.files.length !== 0) {
        let data = new FormData();
        data.append("media-file", fileField.files[0]);
        data.append("mediaType", "IMAGE");
        let resp = await fetch("/media/upload", {
            method: "POST",
            body: data,
        });
        let status = resp.status
        resp = await resp.text();
        await fetchUsersMedia("image");
        if (status !== STATUS_OK) {
            document.getElementById("insertPreviewError").innerText =
                "Что-то пошло не так. Попробуйте сжать медиа и повторить попытку."
            document
                .getElementById("insertPreviewError")
                .removeAttribute("hidden");
        } else if (resp.startsWith("Error: ")) {
            document.getElementById("insertPreviewError").innerText =
                resp.replace("Error: ", "");
            document
                .getElementById("insertPreviewError")
                .removeAttribute("hidden");
        } else {
            let files = document
                .getElementById("previewGallery")
                .getElementsByTagName("input");
            for (let i = 0; i < files.length; ++i) {
                if (files[i].value === resp) {
                    files[i].setAttribute("checked", "checked");
                    break;
                }
            }
        }
        try {
            fileField.value = "";
            if (fileField.value) {
                fileField.type = "text";
                fileField.type = "file";
            }
        } catch (e) {
        }
    }
    await fetchUsersMedia("image", "previewGallery");
}


const hashTagOpt = document.getElementById("hashtag");
const hashTagInp = document.getElementById("hashtag-input");

function updateHashTag() {
    if (hashTagOpt.value === "-1") {
        hashTagInp.removeAttribute("hidden");
    } else {
        hashTagInp.setAttribute("hidden", "hidden");
    }
}

hashTagOpt.addEventListener("change", updateHashTag);

updateHashTag();