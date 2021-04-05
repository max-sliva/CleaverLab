let conStart = null;
const width = window.innerWidth;
// var height = window.innerHeight-100;
const height = window.innerHeight;
const stage = new Konva.Stage({
    container: 'container',
    width: width,
    height: height - 250
});
const layer = new Konva.Layer();

let objTargets = [];
stage.add(layer);
let group = new Konva.Group({ //группа для объектов Konva, чтоб двигать объекты (RasPi+usb)
    x: 20,
    y: 0,
    draggable: true,
});
let i = 0;
let arduStringName = new Map();
let objMap = new Map();
let connMap = new Map();
let objMap2 = new Map();
let connMap2 = new Map();
let connMapArduDev = new Map();

let j = 0;
let curArdu = null;
let curDevice = null;

function blueBoxClick() { //клик по устройству с синей рамкой
    console.log("!device dblclick!")
}


function drawDevice(imageObj, x = 0, y = 0) { //для отрисовки девайса
    let imgId = imageObj.src;
    let x1 = x;
    let y1 = y;
    if (x === 0) x1 = 220 + 20 + (j++) * 5;

    const slashN = imgId.lastIndexOf("/");
    imgId = imgId.substring(slashN + 1, (imgId.length - 4)) + "#" + j;
    console.log("added img id = ", imgId);
    let img = new Konva.Image({ //сам девайс
        image: imageObj,
        // x: 220+20+(j++)*5,
        x: x1,
        // y: 0,
        y: y1,
        // width: 100,
        // height: 137,
        draggable: true,
        id: imgId
    });
    img.on('click', function () { //для добавления синей рамки девайса при клике на нем
        const box1 = stage.find('.blueBox'); //находим предудущую синюю рамку
        // console.log("box = ", box);
        box1.destroy();  //убираем ее
        let box = new Konva.Rect({  //новая синяя рамка у текущего объекта
            name: "blueBox",
            x: img.x(),
            y: img.y(),
            width: img.width() + 1,
            height: img.height() + 1,
            // fill: 'white',
            stroke: 'blue',
            strokeWidth: 2,
        });
        box.on('click', blueBoxClick);
        let delBtn = document.getElementById("btnRemove");
        delBtn.disabled = false;
        console.log("btn text = ", delBtn);
        curDevice = img;
        let box2 = layer.find('.redBox');
        if (box2[0] !== undefined && !objMap.has(box2[0])) {
            let newLine = new Konva.Line({ //создаем новую линию от ардуины до девайса
                points: [curArdu.x() + curArdu.width(), curArdu.y() + curArdu.height() / 2, img.x(), img.y() + img.height() / 2],
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
        console.log("click! x=", img.x(), "i=", i);
        layer.add(box);
    });
    objTargets.push(img);
    layer.add(img);
    stage.add(layer);
    console.log("objTargets size = ", objTargets.length)
    objTargets.forEach((target) => { //цикл для обновления линий
        target.on('dragmove', updateLine2);
    });

    return img;
}

function drawImage(imageObj, x = 0, y = 0, ardu_number = -1) { //создание Konva.Image с нужным изображением
    //-1 - для создания объекта в режиме редактирования
    //иначе объекты загружаются из сохранения
    let x1 = 0, y1 = 0;
    let i_ardu = i;
    if (ardu_number > 0) { //при загрузке ардуино из сохранения
        i_ardu = ardu_number;
        x1 = x;
        y1 = y;
    } else {
        x1 = i_ardu == 0 ? 20 : 220 + i_ardu * 2; //расположение по оси ОХ на канвасе
        y1 = 0;
    }
    imgId = (i_ardu > 0)?"ardu" + "#" + i_ardu:"rasPi"; //выбор между ардуино и малиной
    console.log("imgId = ", imgId);
    let img = new Konva.Image({
        image: imageObj,
        // x: i==0?20:220+i*2,
        x: x1,
        // y: 0,
        y: y1,
        width: 200,
        height: 137,
        id: imgId
        // draggable: true,
    });
    console.log("img.x = ",img.x());
    console.log("img.y = ",img.y());
    console.log("img.width = ",img.width());
    // console.log("img.y = ",img.y());
    if (i_ardu > 0) { //если выбрали ардуино
        // img.id = "ardu" + "#" + i;
        img.setDraggable(true);
        console.log("2nd image");
        img.on('click', function () { //для добавления красной рамки ардуино
            const box1 = stage.find('.redBox'); //находим предудущую красную рамку
            // console.log("box = ", box);
            box1.destroy();  //убираем ее
            let box = new Konva.Rect({  //новая красная рамка у текущего объекта
                name: "redBox",
                x: img.x(),
                y: img.y(),
                width: img.width() + 1,
                height: img.height() + 1,
                // fill: 'white',
                stroke: 'red',
                strokeWidth: 2,
            });
            let delBtn = document.getElementById("btnRemove");
            delBtn.disabled = false;

            // group.add(box);
            curArdu = img; //текущая выбранная ардуино
            console.log("click! x=", curArdu.x(), "i=", i_ardu);
            layer.add(box);
        });
    } else { //если выбрали малину
        img.on('click', function () { //для удаления красной рамки ардуино
            console.log("raspi click!");
            const redBox = stage.find('.redBox');
            // console.log("box = ", box);
            redBox.destroy();
            const blueBox = stage.find('.blueBox');
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

let usbPorts = [];
let portNum = -1;
function makeRasPi() {
    group = new Konva.Group({ //группа для объектов Konva, чтоб двигать объекты (RasPi+usb)
        x: 20,
        y: 0,
        draggable: true,
    });
    let raspiImg = new Image();
    raspiImg.onload = function () { //вызывается при загрузке изображения малины
        const img = drawImage(this); //добавляем на канвас
        group.add(img); //добавляем в группу
        // objTargets.push(group);
        for (let _port = 0; _port < 4; _port++) { //цикл для юсб-портов
            let box = new Konva.Rect({ //рамка вокруг надписи
                x: img.width() - 25,
                y: img.y() + 5 + (_port) * 20,
                width: 40,
                height: 15,
                name: 'USB' + _port,
                fill: 'white',
                stroke: 'black',
                strokeWidth: 2,
            });
            group.add(box);
            const usb = new Konva.Text({ //надписи USB
                text: 'USB' + _port,
                x: img.width() - 24,
                y: img.y() + 6 + (_port) * 20,
                fontSize: 14,
                width: 200,
            });
            usbPorts.push(usb);
            usb.on('click', function () { //при клике на юсб-порт
                conStart = this;
                console.log('selected ', conStart.text());
                portNum = usbPorts.indexOf(conStart); //получаем номер порта в массиве
                console.log("i = ", portNum);
                console.log("portsSize = ", usbPorts.length);

                // line.points([group.x()+objTargets[0].width()+20, group.y()+(portNum*box.height())+(portNum+1)*(box.height()/2), objTargets[1].x(), objTargets[1].y()+objTargets[1].height()/2]);
                let box2 = layer.find('.redBox'); //находим объект с красной рамкой
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
                if (box2[0] !== undefined && !objMap.has(box2[0]) && !usbInUse) {
                    let newLine = new Konva.Line({ //создаем новую линию от малины до ардуины
                        // points: [group.x()+objTargets[0].width()+20, 5+group.y()+(portNum*usbPorts[0].height())+(portNum+1)*(usbPorts[0].height()/2), box2[0].x(), box2[0].y()+box2[0].height()/2],
                        points: [group.x() + objTargets[0].width() + 20, 5 + group.y() + (portNum * usbPorts[0].height()) + (portNum + 1) * (usbPorts[0].height() / 2), box2[0].x(), box2[0].y() + 40],
                        stroke: 'green',
                        strokeWidth: 2,
                    });
                    //TODO здесь сделать запись о соединении порта малины с ардуино, чтоб потом передавать на сервер для сохранения в БД
                    if (objMap.get(curArdu) != null) { //если текущая картинка уже соединена с малиной
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
        group.on('dragmove', updateLine);
    };
    raspiImg.src = 'RaspberryPi_3B.png';
}

makeRasPi();
const arduImg = new Image();
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
        const x1 = group.x() + objTargets[0].width() + 20;
        const y1 = 5 + group.y() + (portNum * usbPorts[0].height()) + (portNum + 1) * (usbPorts[0].height() / 2);
        const x2 = obj.x();
        // const y2 = obj.y()+obj.height()/2;
        const y2 = obj.y() + 40;
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
        const x1 = arduObj.x() + arduObj.width();
        const y1 = arduObj.y() + arduObj.height() / 2;
        const x2 = obj.x();
        // const y2 = obj.y()+obj.height()/2;
        const y2 = obj.y() + obj.height() / 2;
        tempLine.points([x1, y1, x2, y2]);
    }
    // console.log('objMap.size = ', objMap.size);
    layer.batchDraw();
}

const container = stage.container();
container.tabIndex = 1;// make it focusable
container.focus();
const DELTA = 4;
container.addEventListener('keydown', function (e) {
    if (e.keyCode === 27) { //для снятия красной и синей рамки при нажатии ESC
        console.log('ESC pressed')
        let box1 = stage.find('.blueBox'); //находим предудущую синюю рамку
        // console.log("box = ", box);
        box1.destroy();  //убираем ее
        box1 = stage.find('.redBox'); //находим предудущую синюю рамку
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

function addArdu() {
    drawImage(arduImg);
}

function addDevice() {
    let device = document.getElementById("devicesCombo")
    console.log("device = ", device.value)
    let deviceImg = new Image()
    deviceImg.onload = function () {
        drawDevice(this);
        objTargets.forEach((target) => { //цикл для обновления линий
            target.on('dragmove', updateLine2);
        });
    }
    deviceImg.src = "" + device.value + ".jpg"
}

function removeDevice() {
    let box1 = stage.find('.blueBox'); //находим предудущую синюю рамку - устройство
    // console.log("box = ", box);
    if (box1 !== undefined) {
        console.log("!deleting device! curDevice = ", curDevice);

        let tempLine = objMap2.get(curDevice); //находим соединительную линию
        if (tempLine !== undefined) tempLine.destroy() //удаляем ее

        if (curDevice != undefined) curDevice.destroy();
        objMap2.delete(curDevice)
        box1.destroy();  //убираем ее
    }
    let box2 = stage.find('.redBox'); //находим предудущую красную рамку - ардуино
    // console.log("box = ", box2);
    if (box2 !== undefined) {
        console.log("!deleting ardu! curArdu = ", curArdu);
        let tempLine = objMap.get(curArdu); //находим соединительную линию
        if (tempLine !== undefined) tempLine.destroy() //удаляем ее

        tempLine = objMap2.get(curArdu); //находим соединительную линию
        if (tempLine !== undefined) tempLine.destroy() //удаляем ее
        objMap.delete(curArdu)
        connMap.delete(curArdu)

        // console.log("connMapArduDev=", connMapArduDev);
        // console.log("objMap2=", objMap2);

        connMap2.forEach(function (value, key) { //для всех соединений ардуино с девайсом
            if (curArdu == value) {  //если это текущее удаляемое ардуино
                const dev = key; //берем девайс
                tempLine = objMap2.get(dev); //находим соединительную линию с этим девайсом
                if (tempLine !== undefined) tempLine.destroy() //удаляем ее
            }
        });

        // const dev = connMapArduDev.get(curArdu);
        // tempLine = objMap2.get(dev); //находим соединительную линию
        // if (tempLine !== undefined) tempLine.destroy() //удаляем ее
        if (curArdu != undefined) {
            curArdu.destroy();
        }
        box2.destroy();
    }
    layer.batchDraw();
}

function saveKanva() { //для сохранения канваса
    let jsonObj = {
        "objects": [
            // {"type":"ardu", "name":"ardu#1", "x":"", "y":""},
            // {"type":"device", "name":"dc_motor#0", "x":"", "y":""}
        ],
        "lines": []
        // {"points": [240, 75, 283, 271], "from": "usb3", "to": "ardu#2"},
        // {"points": [514, 89.5, 644, 102], "from": "ardu#1", "to": "dc_motor#0"},
    };
    console.log("connMap2 = \n")
    connMap2.forEach(function (value, key) {
        console.log("key=", key, "  value=", value);
    });
    console.log("connMap2 end")
    // console.log("objMap2 for devices: \n")
    objMap2.forEach(function (value, key) { //для всех девайсов
        // console.log("key=", key, "  value=", value);
        // console.log("imgId = ", key.id())
        jsonObj.objects.push({"type": "device", "name": key.id(), "x": key.x(), "y": key.y(), "ardu_name": connMap2.get(key).id()})
    });
    // console.log("objMap for arduinos: \n");
    let lineN = 0;
    objMap.forEach(function (value, key) { //для всех ардуино
        // console.log("key=", key, "  value=", value);
        // console.log("imgId = ", key.id())
        jsonObj.objects.push({"type": "ardu", "name": key.id(), "x": key.x(), "y": key.y(), "usb": connMap.get(key)})
        //для линий
        lineN++;
        console.log("line.points = ", value.points());
        jsonObj.lines.push({"points": value.points()});
        let curArdu = key;
        // const dev = connMapArduDev.get(key);
        //todo добавить удаление занятого ЮСБ из массива портов
        //todo добавить from и to
        connMap2.forEach(function (value, key) { //для всех соединений ардуино с девайсом
            if (curArdu == value) {  //если это текущая  ардуино
                const dev = key; //берем девайс
                let tempLine = objMap2.get(dev); //находим соединительную линию с этим девайсом
                if (tempLine !== undefined) {
                    lineN++;
                    console.log("lineTodev.points = ", tempLine.points());
                    jsonObj.lines.push({"points": tempLine.points()});
                }
            }
        });
    });
    console.log("jsonObj = ", jsonObj);
    console.log("lineN = ", lineN);
    //todo сделать передачу на сервер
}

// function deviceLoad(deviceImg){
//     deviceImg.onload = function () {
//         curImg = drawImage(this, item.x, item.y, ardu_number);
//         //objTargets.push(curImg);
//         console.log("item.name = ", item.name);
//         arduStringName.set(item.name, curImg);
//     }
// }

function loadArduinos(jsonObj){  //для загрузки ардуин
     jsonObj.objects.forEach(async function (item) {  //цикл по json-массиву
        // console.log(item);
        switch (item.type) {
            case "ardu": {  //если объект - ардуино
                let curImg;
                let imgName = item.name.substring(0, item.name.lastIndexOf("#"));
                const ardu_number = item.name.substring(item.name.lastIndexOf("#") + 1, item.name.length);
                console.log("imgName = ", imgName);
                console.log("ardu_number = ", ardu_number);
                let deviceImg = new Image();
                deviceImg.src = 'iskra2.jpg';
                // deviceLoad(deviceImg);
                deviceImg.onload = async function () {
                    curImg = await drawImage(this, item.x, item.y, ardu_number);
                    //objTargets.push(curImg);
                    console.log("item.name = ", item.name);
                    arduStringName.set(item.name, curImg);
                    loadDevices(jsonObj, item.name); //загружаем девайсы для этой ардуино
                    jsonObj.lines.forEach(function (itemLine) { //цикл по линиям
                        if (item.name === itemLine.to) {
                            console.log("itemLine.from = ", itemLine.from);
                            // curArdu = arduStringName.get(itemLine.from);
                            // console.log("curArdu from device = ", curArdu);
                            drawLine(itemLine,null, curImg, item.usb);
                            console.log("line added");
                        }
                    });

                }
            }
            break;
        }
    });

}

function loadDevices(jsonObj, arduName){  //для загрузки устройств
    jsonObj.objects.forEach(function (item) {
        // console.log(item);
        switch (item.type) {
            case "device": {   //если устройство
                if (item.ardu_name===arduName){ //и строки с указанной ардуино совпадают
                    let curImg; //текущимй объект с картинкой
                    let imgName = item.name.substring(0, item.name.lastIndexOf("#"));
                    console.log("imgName = ", imgName);
                    let deviceImg = new Image();
                    deviceImg.src = imgName + ".jpg";
                    deviceImg.onload = function () {
                        curImg = drawDevice(this, item.x, item.y);
                        //objTargets.push(curImg);
                        jsonObj.lines.forEach(function (itemLine) { //цикл по линиям
                            if (item.name === itemLine.to) {
                                console.log("itemLine.from = ", itemLine.from);
                                curArdu = arduStringName.get(itemLine.from);
                                console.log("curArdu from device = ", curArdu);
                                drawLine(itemLine, curImg, curArdu, null);
                                console.log("line added");
                            }
                        });
                    }
                    //j++;
                    break;
                }
            }
        }
    });
}

async function loadKanva() { //загрузка объектов из сохранения
    clearKonva();
    let jsonObj = {
        "objects": [
            {"type": "ardu", "name": "ardu#1", "x": 314, "y": 21, "usb": 0},
            {"type": "ardu", "name": "ardu#2", "x": 264, "y": 188, "usb": 3},
            {"type": "device", "name": "dc_motor#0", "x": 644, "y": 38, "ardu_name":"ardu#1"},
            {"type": "device", "name": "servo#1", "x": 626, "y": 198, "ardu_name":"ardu#1"},
            {"type": "device", "name": "rgb_matrix#3", "x": 489, "y": 291, "ardu_name":"ardu#2"}
        ],
        "lines": [
            {"points": [240, 12, 314, 61], "from": "usb0", "to": "ardu#1" },
            {"points": [240, 75, 283, 271], "from": "usb3", "to": "ardu#2"},
            {"points": [514, 89.5, 644, 102], "from": "ardu#1", "to": "dc_motor#0"},
            {"points": [514, 89.5, 626, 262], "from": "ardu#1", "to": "servo#1"},
            {"points": [464, 256.5, 489, 351.5], "from": "ardu#2", "to": "rgb_matrix#3"}
        ]
    };

    await loadArduinos(jsonObj); //загружаем ардуины
    // let newLine = new Konva.Line({ //создаем новую линию от ардуины до девайса
    //     points: [curArdu.x() + curArdu.width(), curArdu.y() + curArdu.height() / 2, img.x(), img.y() + img.height() / 2],
    //     stroke: 'green',
    //     strokeWidth: 2,
    // });
    // jsonObj.lines.forEach(function (item) {
    //     drawLine(item);
    // });
}

function drawLine(line, deviceImg = null, curArdu, portNum){ //для рисования линии из сохранения
    let newLine = new Konva.Line({ //создаем новую линию от ардуины до девайса
        points: line.points,
        stroke: 'green',
        strokeWidth: 2,
    });
    if (deviceImg!=null){ //если рисуем линию от ардуино к устройству
        objMap2.set(deviceImg, newLine); //добавляем набор девайс-линия
        connMap2.set(deviceImg, curArdu); //добавляем набор девайс-ардуино
        connMapArduDev.set(curArdu, deviceImg);
    } else {  //если рисуем линию от малины к ардуино
        objMap.set(curArdu, newLine); //добавляем набор картинка-линия
        connMap.set(curArdu, portNum); //добавляем набор картинка-порт
    }
    layer.add(newLine);
    layer.batchDraw();
}

function clearKonva(){
    layer.destroyChildren();
    i = 0;
    usbPorts = [];
    objTargets = [];  //обнуляем все массивы и мапы
    objMap = new Map();
    connMap = new Map();
    objMap2 = new Map();
    connMap2 = new Map();
    connMapArduDev = new Map();
    arduStringName = new Map();
    portNum = 0;
    j = 0;
    curArdu = null;
    curDevice = null;

    makeRasPi();
    layer.batchDraw();
}