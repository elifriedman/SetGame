console.log("yo");

//start game
//creates global set card converter array
//sends request to server for initial cards
//asks for unique game id
cardConverterArray = [];
requestTimer = null;
requestDelay = 5000; //ms
gameDiv = null;

boardWidth = 7;
boardHeight = 3;
boardGrid = null;

uid = "";
gid = "";

currSelectedList = [];
currSelectedCells = []; //corresponding list to html elems that you clicked on
stateNum = -1; //initialized to -1 so that when you start up game, you automattically get an update
hasUpdate = false; //this turns true when three cards are selected. At next request to server, it is set to false.

function init() {
    gameDiv = $('#setboard');
    createGameBoard();
    getUrlParams();
    cardConverterArray = createCardArray();
    
    //initBoard
    deleteBoard();
    
    addCard(72,6,2);
    addCard(43,0,0);
    addCard(56,4,1);
    //eventHandlers
    $('.setCard').click(function() {
	cell = $(this).parent();
        x = cell.data('x');
        y = cell.data('y');
        console.log("clicked x:" + x + " y:" + y);
	
	cardCode = cell.find('.setCard').data('cardcode');
	cardIndex = currSelectedList.indexOf(cardCode);
	if (cardIndex == -1) {
		cell.css('background','yellow');
		currSelectedList.push(cardCode);
		currSelectedCells.push(cell);

		if (currSelectedList.length >= 3) {
			hasUpdate = true;
		}
        	console.log(currSelectedList);
	} else {
		cell.css('background','none');
		currSelectedList.splice(cardIndex,1); //delete that card
		currSelectedCells.splice(cardIndex,1);
	}	


	//removeCard(x,y);
    });

    requestTimer = window.setInterval(function () {
	

	if (hasUpdate || stateNum == -1) {
		requestObj = {uid:uid,gameId:gid,set:currSelectedList,hasUpdate:hasUpdate,stateNum:stateNum};
		console.log("udpate!");
		console.log(requestObj);

		
		undoSetSelect();
	} else {
		requestObj = {gameId:gid,hasUpdate:hasUpdate, stateNum:stateNum};
	}
	$.ajax({
		type: "POST",
		url: "/gameDATA",
		dataType: "json",
		contentType: 'application/json; charset=UTF-8',
		data: JSON.stringify(requestObj),
		    success: function (data, textStatus, jqXHR) {
			handleUpdate(data);
			stateNum; //should actually read stateNum value from data
		     }
        });
    },requestDelay);    

    
}

//4D loop
//creates strings with four letters. letters represent properties of each card
//[0] = shape
//[1] = color
//[2] = number
//[3] = fill
function createCardArray() {
    tempCardConvArray = [];
    count = 0;
    for (var s = 0; s < 3; s++) {function getUrlParameter(sParam)
{
    var sPageURL = window.location.search.substring(1);
    var sURLVariables = sPageURL.split('&');
    for (var i = 0; i < sURLVariables.length; i++) 
    {
        var sParameterName = sURLVariables[i].split('=');
        if (sParameterName[0] == sParam) 
        {
            return sParameterName[1];
        }
    }
} 
        for (var c = 0; c < 3; c++) {
            for (var n = 0; n < 3; n++) {
                for (var f = 0; f < 3; f++) {
                    tempCardConvArray[count] = (""+s)+(""+c)+(""+n)+(""+f); //beautiful, untyped language!
                    count++;
                }
            }
        }
    }
    return tempCardConvArray;
}

function createGameBoard() {
    gameTable = $("#setboard");
    for (i=0; i<3; i++) {
	tempRow = $('<tr></tr>');
	for (j=0; j<7; j++) {
	    tempRow.append("<td class=setCell data-x=" + j + " data-y=" + i + "></td>");
	}
	gameTable.append(tempRow);
    }
}

function getUrlParams() {
	uid = getUrlParam('uid');
	gid = getUrlParam('gid');
}

function getUrlParam(sParam)
{
    var sPageURL = window.location.search.substring(1);
    var sURLVariables = sPageURL.split('&');
    for (var i = 0; i < sURLVariables.length; i++) 
    {
        var sParameterName = sURLVariables[i].split('=');
        if (sParameterName[0] == sParam) 
        {
            return sParameterName[1];
        }
    }
} 

function addCard(cardCode,x,y) {
    $("td[data-x=" + x + "][data-y=" + y + "]").append('<div class=setCard data-cardcode=' + cardCode + '>'+ cardCode + '</div>');
    boardGrid[x][y] = cardCode;
}

function removeCard(x,y) {
    $("td[data-x=" + x + "][data-y=" + y + "]").empty();
    boardGrid[x][y] = -1;
}

function deleteBoard() {
    boardGrid = new Array(boardWidth);
    for (i = 0; i < boardWidth; i++) {
        boardGrid[i] = new Array(boardHeight);
        for (j = 0; j < boardHeight; j++) {
            boardGrid[i][j] = -1;
        }
    }
}

function handleUpdate(data) {
	if (data["hasUpdate"]) {
		deleteBoard();
		stateNum = data["stateNum"];
		cards = data["cards"];
		for (i = 0; i < cards.length; i++) {
			tempCardCode = parseInt(cards[i],3); //trinary
			x = i%boardWidth;
			y = Math.floor(i/boardWidth);			
			addCard(tempCardCode,x,y);
		}
	}
}

//when you have selected 3 cards and an update has been requested, deselect cards
function undoSetSelect() {
	hasUpdate = false;
	currSelectedList = [];
	for (i = 0; i < currSelectedCells.length; i++) {
		currSelectedCells[i].css('background','none');
	}
	
}

$(function() {
    init();
});
