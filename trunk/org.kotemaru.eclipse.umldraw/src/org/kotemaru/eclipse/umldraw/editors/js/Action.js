

function Action(){this.initialize.apply(this, arguments)};
(function(_class){

	var $menu;
	var dragItem;
	var ox;
	var oy;
	var rx;
	var ry;
	
	_class.prototype.initialize = function(targetClass) {
		this.targetClass = targetClass;
	}
	
	_class.dragStart = function(item, evx,evy) {
		dragItem = item;
		ox = evx;
		oy = evy;
		rx = evx - item.x();
		ry = evy - item.y();
		dragItem.dragStart(evx-rx,evy-ry);
	}
	
	_class.dragMove = function(evx,evy) {
		if (dragItem == null) return;
		dragItem.dragMove(evx-rx,evy-ry);
		Canvas.refresh();
	}
	_class.dragEnd = function(evx,evy) {
		if (dragItem == null) return;
		dragItem.dragEnd(evx-rx,evy-ry);
		dragItem = null;
		Canvas.refresh();
	}
	
	function selectAndDrag(ex,ey) {
		var elem = Canvas.getHandle(ex,ey);
		if (elem == null) {
			elem = Canvas.getItem(ex,ey);
			if (elem) Canvas.select(elem);
		}
		if (elem && elem.isDraggable) {
			_class.dragStart(elem, ex,ey);
		}
		Canvas.refresh();
	}
	
	//-----------------------------------------------------------
	_class.prototype.commandSelect = function() {
		if (this.targetClass) {
			Canvas.cursor("new");
		} else {
			Canvas.cursor("");
		}
	}

	_class.prototype.onMouseDown = function(ev) {
		if (this.targetClass) {
			var item = new this.targetClass({x:ev.offsetX, y:ev.offsetY});
			Canvas.addItem(item);
			Canvas.refresh();
		} else {
			selectAndDrag(ev.offsetX, ev.offsetY);
		}
	}

	_class.prototype.onMouseMove = function(ev) {
		_class.dragMove(ev.offsetX, ev.offsetY);
	}
	_class.prototype.onMouseUp  = function(ev) {
		//Canvas.select();
		_class.dragEnd(ev.offsetX, ev.offsetY);
	}
	
	_class.prototype.openMenu = function(ev) {
		if ($menu) $menu.hide();
		var item = Canvas.getItem(ev.offsetX, ev.offsetY);
		if (item == null) {
			// TODO: canvas menu.
		} else if (item.getMenu) {
			Canvas.select(item);
			Canvas.refresh();
			$menu = item.getMenu(ev.offsetX, ev.offsetY);
			$menu.show();
			$menu.offset({left:ev.clientX, top:ev.clientY});
			$menu.find(".MenuItem").unbind("mouseup").bind("mouseup",function(){
				item.doMenuItem($(this),ev.offsetX, ev.offsetY);
				$menu.hide();
				Canvas.refresh();
			});
		}
	}
	
	_class.prototype.onDblClick  = function(ev) {
		var item = Canvas.getItem(ev.offsetX, ev.offsetY);
		if (item == null) {
			// TODO: canvas menu.
			var data = {svg: Canvas.toSVG()};
			Dialog.open("#dialogSVG", data);
			var ifr = $("#iframeSvg")[0];
			ifr.contentDocument.body.innerHTML = data.svg;
		} else if (item.getDialog) {
			Canvas.select(item);
			Canvas.refresh();
			Dialog.open(item.getDialog(), item);
		}
		
	}
	
})(Action);


//EOF