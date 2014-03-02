//<![CDATA[
var accordionItems = new Array();

function initAccordian() {
    var divs = document.getElementsByTagName('div');
    // Grab the accordion items from the page
    for (var i = 0; i < divs.length; i++) {
        if (divs[i].className == 'accordionItem')
            accordionItems.push(divs[i]);
    }
    // Assign onclick events to the accordion item headings
    for (var i = 0; i < accordionItems.length; i++) {
        var h2 = getFirstChildWithTagName(accordionItems[i], 'H2');
        h2.onclick = toggleItem;
    }
    // Hide all accordion item bodies except the first
    for (var i = 0; i < accordionItems.length; i++) {
        accordionItems[i].className = 'accordionItem hide';
    }
}

function toggleItem() {
	var itemClass = this.parentNode.className;
	if (itemClass == 'accordionItem hide') {
		this.parentNode.className = 'accordionItem';
	}
	else
	if (itemClass == 'accordionItem') {
		this.parentNode.className = 'accordionItem hide';
	}
}

function getFirstChildWithTagName(element, tagName) {
    for (var i = 0; i < element.childNodes.length; i++) {
        if (element.childNodes[i].nodeName == tagName)
            return element.childNodes[i];
    }
}
 //]]>
