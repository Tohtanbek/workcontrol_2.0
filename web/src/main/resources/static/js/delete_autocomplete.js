let inp = document.querySelectorAll("input");
for (let i=0; i<inp.length; i++){
    inp[i].spellcheck=false;
    inp[i].autocomplete="one-time-code"
}
