/*
 * Copyright (c) 2023 Christian Kierdorf
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the “Software”),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 */
/* Wenn man auf der Navbar-Toggler klickt, zeigt sich der Menüliste. */
document.addEventListener("DOMContentLoaded", function() {
  let navbarToggler = document.querySelector(".navbar-toggler");
  let menu = document.querySelector(".menu ul");
  navbarToggler.addEventListener("click", function() {
     if (!menu.style.display || menu.style.display === "none") {
            menu.style.display = "block";
        } else {
            menu.style.display = "none";
        }
      });
});
/*
// MainModel Test
import { MainModel } from './modules/model.js';
console.log("Test MainModel")
let mainModel = new MainModel();
mainModel.setStoredMicrophoneIndex(1);
mainModel.setStoredAlgorithmIndex(2);
mainModel.setStoredKeyIndex(3);
mainModel.setStoredConcertPitchIndex(100);
mainModel.setStoredTuneIndex(42);
console.log(mainModel.getString());
let newMainModel = MainModel.createFromString(mainModel.getString());
console.assert(1===newMainModel.getStoredMicrophoneIndex());
console.assert(2===newMainModel.getStoredAlgorithmIndex());
console.assert(3===newMainModel.getStoredKeyIndex());
console.assert(100===newMainModel.getStoredConcertPitchIndex());
console.assert(42===newMainModel.getStoredTuneIndex());
console.log(newMainModel.getString());

// Harmonica Test
import { RichterHarmonica } from './modules/harmonica.js';
import { AbstractHarmonica } from './modules/harmonica.js';
console.log("Test RichterHarmonica")
let harmonica = new RichterHarmonica();
console.assert(harmonica.getBlowBendingTonesCount(1)===0);
console.assert(harmonica.getBlowBendingTonesCount(2)===0);
console.assert(harmonica.getBlowBendingTonesCount(3)===0);
console.assert(harmonica.getBlowBendingTonesCount(4)===0);
console.assert(harmonica.getBlowBendingTonesCount(5)===0);
console.assert(harmonica.getBlowBendingTonesCount(6)===0);
console.assert(harmonica.getBlowBendingTonesCount(7)===0);
console.assert(harmonica.getBlowBendingTonesCount(8)===1);
console.assert(harmonica.getBlowBendingTonesCount(9)===1);
console.assert(harmonica.getBlowBendingTonesCount(10)===2);

import { HarmonicMollHarmonica } from './modules/harmonica.js';
console.log("Test HarmonicMollHarmonica")
harmonica = new HarmonicMollHarmonica();
console.assert(harmonica.getBlowBendingTonesCount(1)===0);
console.assert(harmonica.getBlowBendingTonesCount(2)===0);
console.assert(harmonica.getBlowBendingTonesCount(2)===0);
console.assert(harmonica.getBlowBendingTonesCount(3)===0);
console.assert(harmonica.getBlowBendingTonesCount(4)===0);
console.assert(harmonica.getBlowBendingTonesCount(5)===0);
console.assert(harmonica.getBlowBendingTonesCount(6)===0);
console.assert(harmonica.getBlowBendingTonesCount(7)===0);
console.assert(harmonica.getBlowBendingTonesCount(8)===0);
console.assert(harmonica.getBlowBendingTonesCount(9)===1);
console.assert(harmonica.getBlowBendingTonesCount(10)===3);

*/