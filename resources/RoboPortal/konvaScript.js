var conStart = null;
var width = window.innerWidth;
// var height = window.innerHeight-100;
var height = window.innerHeight;
var stage = new Konva.Stage({
    container: 'container',
    width: width,
    height: height-300,
});
var layer = new Konva.Layer();

var objTargets = [];
stage.add(layer);
var group = new Konva.Group({ //группа для объектов Konva, чтоб двигать объекты (RasPi+usb)
    x: 20,
    y: 0,
    draggable: true,
});
let i = 0;
let objMap = new Map();
let connMap = new Map();
let objMap2 = new Map();
let connMap2 = new Map();
let connMapArduDev = new Map();

let j = 0;
let curArdu = null;
let curDevice = null;
function drawDevice(imageObj){ //для отрисовки девайса
    let imgId = imageObj.src;
    const slashN = imgId.lastIndexOf("/");
    imgId = imgId.substring(slashN+1,(imgId.length-4))+j;
    console.log("added img id = ", imgId);
    var img = new Konva.Image({ //сам девайс
        image: imageObj,
        x: 220+20+(j++)*5,
        y: 0,
        // width: 100,
        // height: 137,
        draggable: true,
        id: imgId
    });
    img.on('click', function(){ //для добавления синей рамки девайса при клике на нем
        const box1=stage.find('.blueBox'); //находим предудущую синюю рамку
        // console.log("box = ", box);
        box1.destroy();  //убираем ее
        let box = new Konva.Rect({  //новая синяя рамка у текущего объекта
            name: "blueBox",
            x: img.x(),
            y: img.y(),
            width: img.width()+1,
            height: img.height()+1,
            // fill: 'white',
            stroke: 'blue',
            strokeWidth: 2,
        });
        let delBtn = document.getElementById("btnRemove");
        delBtn.disabled = false;
        console.log("btn text = ",delBtn);
        curDevice = img;
        let box2=layer.find('.redBox');
        if  (box2[0]!==undefined && !objMap.has(box2[0])) {
            let newLine = new Konva.Line({ //создаем новую линию от ардуины до девайса
                points: [curArdu.x()+curArdu.width(), curArdu.y()+curArdu.height()/2, img.x(), img.y() + img.height()/2],
                stroke: 'green',
                strokeWidth: 2,
            });
            //TODO здесь сделать запись о соединении девайса с ардуиной и портом малины и отправить на сервер для записи в БД
            if (objMap2.get(img) != null) { //если текущая картинка уже соединена с ардуино
                let tempLine = objMap2.get(img); //находим соединительную линию
                tempLine.destroy() //удаляем ее
                layer.draw();
                stage.add(layer);
                console.log("!!2 lines!!")
            }
            objMap2.set(img, newLine); //добавляем набор девайс-линия
            connMap2.set(img, curArdu); //добавляем набор девайс-ардуино
            connMapArduDev.set(curArdu, img);
            layer.add(newLine);
            layer.batchDraw();
        }
        // group.add(box);
        // curImage = img;
        console.log("click! x=",img.x(), "i=",i);
        layer.add(box);
    });
    objTargets.push(img);
    layer.add(img);
    stage.add(layer);
}

function drawImage(imageObj) { //создание Konva.Image с нужным изображением
    var img = new Konva.Image({
        image: imageObj,
        x: i==0?20:220+i*2,
        y: 0,
        width: 200,
        height: 137,
        // draggable: true,
    });
    if (i>0) { //если выбрали ардуино
        img.id = "ardu"+i;
        img.setDraggable(true);
        console.log("2nd image");
        img.on('click', function(){ //для добавления красной рамки ардуино
            const box1=stage.find('.redBox'); //находим предудущую красную рамку
            // console.log("box = ", box);
            box1.destroy();  //убираем ее
            let box = new Konva.Rect({  //новая красная рамка у текущего объекта
                name: "redBox",
                x: img.x(),
                y: img.y(),
                width: img.width()+1,
                height: img.height()+1,
                // fill: 'white',
                stroke: 'red',
                strokeWidth: 2,
            });
            let delBtn = document.getElementById("btnRemove");
            delBtn.disabled = false;

            // group.add(box);
            curArdu = img; //текущая выбранная ардуино
            console.log("click! x=",curArdu.x(), "i=",i);
            layer.add(box);
        });
    } else { //если выбрали малину
        img.on('click', function (){ //для удаления красной рамки ардуино
            console.log("raspi click!");
            const redBox=stage.find('.redBox');
            // console.log("box = ", box);
            redBox.destroy();
            const blueBox=stage.find('.blueBox');
            // console.log("box = ", box);
            blueBox.destroy();
            let delBtn = document.getElementById("btnRemove");
            delBtn.disabled = true;
            curDevice = undefined;
            curArdu = undefined;
        });
    }

    i++;
    img.on('mouseover', function () {
        document.body.style.cursor = 'pointer';
    });
    img.on('mouseout', function () {
        document.body.style.cursor = 'default';
    });
    layer.add(img);
    stage.add(layer);
    objTargets.push(img);
    img.on('dragmove', updateLine);
    return img;
}

var raspiImg = new Image();
var usbPorts = [];
let portNum = -1;
raspiImg.onload = function () { //вызывается при загрузке изображения малины
    const img = drawImage(this); //добавляем на канвас
    group.add(img); //добавляем в группу
    // objTargets.push(group);
    for (var i = 0; i < 4; i++) { //цикл для юсб-портов
        let box = new Konva.Rect({ //рамка вокруг надписи
            x: img.width() - 25,
            y: img.y() + 5 + (i) * 20,
            width: 40,
            height: 15,
            name: 'USB' + i,
            fill: 'white',
            stroke: 'black',
            strokeWidth: 2,
        });
        group.add(box);
        var usb = new Konva.Text({ //надписи USB
            text: 'USB'+i,
            x: img.width() - 24,
            y: img.y() + 6 + (i)*20,
            fontSize: 14,
            width: 200,
        });
        usbPorts.push(usb);
        usb.on('click', function () { //при клике на юсб-порт
            conStart = this;
            console.log('selected ',conStart.text());
            portNum = usbPorts.indexOf(conStart); //получаем номер порта в массиве
            console.log("i = ", portNum);
            // line.points([group.x()+objTargets[0].width()+20, group.y()+(portNum*box.height())+(portNum+1)*(box.height()/2), objTargets[1].x(), objTargets[1].y()+objTargets[1].height()/2]);
            let box2=layer.find('.redBox'); //находим объект с красной рамкой
            // console.log("box=", box2[0].name());
            let usbAr = connMap.values(); //массив из мапов
            // console.log('!usb in map = ',usbAr);
            let usbInUse = false;
            for (let value of usbAr) { //цикл по массиву юсб
                // console.log("usb = ",value);
                if (value == portNum) usbInUse = true; //если выбранный порт уже занят
            }
            console.log('usbInUse = ', usbInUse);
            //условие для корректной отрисовки линии от малины до ардуино
            if  (box2[0]!==undefined && !objMap.has(box2[0]) && !usbInUse)  {
                let newLine = new Konva.Line({ //создаем новую линию от малины до ардуины
                    // points: [group.x()+objTargets[0].width()+20, 5+group.y()+(portNum*usbPorts[0].height())+(portNum+1)*(usbPorts[0].height()/2), box2[0].x(), box2[0].y()+box2[0].height()/2],
                    points: [group.x()+objTargets[0].width()+20, 5+group.y()+(portNum*usbPorts[0].height())+(portNum+1)*(usbPorts[0].height()/2), box2[0].x(), box2[0].y()+40],
                    stroke: 'green',
                    strokeWidth: 2,
                });
                //TODO здесь сделать запись о соединении порта малины с ардуино, чтоб потом передавать на сервер для сохранения в БД
                if (objMap.get(curArdu)!=null){ //если текущая картинка уже соединена с малиной
                    let tempLine = objMap.get(curArdu); //находим соединительную линию
                    tempLine.destroy() //удаляем ее
                    layer.draw();
                    stage.add(layer);
                    console.log("!!2 lines!!")
                }
                objMap.set(curArdu, newLine); //добавляем набор картинка-линия
                connMap.set(curArdu, portNum); //добавляем набор картинка-порт
                layer.add(newLine);
                layer.batchDraw();
            }
        });
        group.add(usb);
    }
    // group.add(usb);
    layer.add(group);
    stage.add(layer);
};
raspiImg.src = 'RaspberryPi_3B.png';
var arduImg = new Image();
arduImg.onload = function () {
    drawImage(this);
    // layer.add(line);
    // stage.add(layer);
    objTargets.forEach((target) => { //цикл для обновления линий
        target.on('dragmove', updateLine);
    });
    group.on('dragmove', updateLine);
};
arduImg.src = 'iskra2.jpg';

function updateLine() { //обновление положения концов линии от малины к ардуино
    // console.log("raspiImage x = ", group.x(), " arduImage x = ", arduImg.x);
    // if (portNum != -1) line.points([group.x()+objTargets[0].width()+20, 5+group.y()+(portNum*usbPorts[0].height())+(portNum+1)*(usbPorts[0].height()/2), objTargets[1].x(), objTargets[1].y()+objTargets[1].height()/2]);
    for (let obj of objMap.keys()) { //цикл по всем объектам мапа
        // console.log('obj = ', obj);
        let tempLine = objMap.get(obj);
        let portNum = connMap.get(obj);
        const x1 = group.x()+objTargets[0].width()+20;
        const y1 = 5+group.y()+(portNum*usbPorts[0].height())+(portNum+1)*(usbPorts[0].height()/2);
        const x2 = obj.x();
        // const y2 = obj.y()+obj.height()/2;
        const y2 = obj.y()+40;
        tempLine.points([x1, y1, x2, y2]);
    }
    // console.log('objMap.size = ', objMap.size);
    layer.batchDraw();
}
function updateLine2() { //обновление положения концов линии от ардуино к девайсу
    // console.log("raspiImage x = ", group.x(), " arduImage x = ", arduImg.x);
    // if (portNum != -1) line.points([group.x()+objTargets[0].width()+20, 5+group.y()+(portNum*usbPorts[0].height())+(portNum+1)*(usbPorts[0].height()/2), objTargets[1].x(), objTargets[1].y()+objTargets[1].height()/2]);
    for (let obj of objMap2.keys()) { //цикл по всем объектам мапа
        // console.log('obj = ', obj);
        let tempLine = objMap2.get(obj);
        let arduObj = connMap2.get(obj);
        const x1 = arduObj.x()+arduObj.width();
        const y1 = arduObj.y()+arduObj.height()/2;
        const x2 = obj.x();
        // const y2 = obj.y()+obj.height()/2;
        const y2 = obj.y()+obj.height()/2;
        tempLine.points([x1, y1, x2, y2]);
    }
    // console.log('objMap.size = ', objMap.size);
    layer.batchDraw();
}

var container = stage.container();
container.tabIndex = 1;// make it focusable
container.focus();
const DELTA = 4;
container.addEventListener('keydown', function (e) {
    if (e.keyCode === 27) { //для снятия красной и синей рамки при нажатии ESC
        console.log('ESC pressed')
        let box1=stage.find('.blueBox'); //находим предудущую синюю рамку
        // console.log("box = ", box);
        box1.destroy();  //убираем ее
        box1=stage.find('.redBox'); //находим предудущую синюю рамку
        // console.log("box = ", box);
        box1.destroy();
        let delBtn = document.getElementById("btnRemove");
        delBtn.disabled = true;
        curDevice = undefined;
        curArdu = undefined;
    } else if (e.keyCode === 46) removeDevice();
    else {
        return;
    }
    e.preventDefault();
    layer.batchDraw();
});

function addArdu(){
    drawImage(arduImg);
}
function addDevice(){
    let device = document.getElementById("devicesCombo")
    console.log("device = ",device.value)
    let deviceImg = new Image()
    deviceImg.onload = function () {
        drawDevice(this);
        objTargets.forEach((target) => { //цикл для обновления линий
            target.on('dragmove', updateLine2);
        });
    }
    deviceImg.src = ""+device.value+".jpg"
}

function removeDevice(){
    let box1=stage.find('.blueBox'); //находим предудущую синюю рамку - устройство
    // console.log("box = ", box);
    if (box1 !== undefined) {
        let tempLine = objMap2.get(curDevice); //находим соединительную линию
        if (tempLine!==undefined) tempLine.destroy() //удаляем ее

        if (curDevice!=undefined) curDevice.destroy();
        objMap2.delete(curDevice)
        box1.destroy();  //убираем ее
    }
    let box2=stage.find('.redBox'); //находим предудущую красную рамку - ардуино
    // console.log("box = ", box2);
    if (box2 != undefined) {
        if (curArdu!=null) {
            curArdu.destroy();
            let tempLine = objMap.get(curArdu); //находим соединительную линию
            if (tempLine!==undefined) tempLine.destroy() //удаляем ее

            tempLine = objMap2.get(curArdu); //находим соединительную линию
            if (tempLine!==undefined) tempLine.destroy() //удаляем ее
            objMap.delete(curArdu)
            connMap.delete(curArdu)
            const dev = connMapArduDev.get(curArdu);
            tempLine = objMap2.get(dev); //находим соединительную линию
            if (tempLine!==undefined) tempLine.destroy() //удаляем ее

        }
        box2.destroy();
    }
    layer.batchDraw();
}

function saveKanva(){ //для сохранения канваса
    let jsonObj = {
        "objects":[
            // {"type":"ardu", "name":"ardu1", "x":"", "y":""},
            // {"type":"device", "name":"DC_motor0", "x":"", "y":""}
        ],
        "lines":[

        ]
    };
    console.log("objMap2 for devices: \n")
    objMap2.forEach(function(value,key){
        console.log("key=", key, "  value=",value);
        console.log("imgId = ", key.id())
        jsonObj.objects.push({"type": "device", "name": key.id(), "x":key.x(), "y":key.y()})
    });
    console.log("objMap for arduinos: \n");
    objMap.forEach(function(value,key){
        console.log("key=", key, "  value=",value);
        console.log("imgId = ", key.id)
        jsonObj.objects.push({"type": "ardu", "name": key.id, "x":key.x(), "y":key.y()})
    });
    console.log("jsonObj = ", jsonObj)
    //todo сделать дерево объектов для сохранения в json и передачи на сервер


}

function loadKanva(){

}