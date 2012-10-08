
function Config(){this.initialize.apply(this, arguments)};
(function(Class){

	var NAMES = [
		"redmineAbsPath",
		"redmineApiPath",
		"redmineApiKey",
		"redmineQueryId",
		"redmineProjectName",
		"redmineProjectId"
	];
	Class.init = function(){
	}
	
	Class.save = function(){
		var config = {};
		for (var i=0; i<NAMES.length; i++) {
			var name = NAMES[i];
			config[name] = getValues($(".Config input[name='"+name+"']"));
		}
		Storage.saveConfig(config);
		setup(config);
	}
	Class.getProjects = function() {
		Class.save();
		MyMine.waiting(true);
		new RedMine().getProjects(function(data){
			var querys = [];
			var names = [];
			for (var i=0; i<data.projects.length; i++) {
				var project = data.projects[i];
				querys.push(project.id);
				names.push(project.name);
			}
			setupProjects({
				redmineProjectId: querys,
				redmineProjectName: names
			});
			MyMine.waiting(false);
		});
	}
	
	function setup(config) {
		for (var name in config) {
			Class[name] = config[name];
			if (name.indexOf("redmineProject") != 0) {
				setValues($("input[name='"+name+"']"),config[name]);
			}
		}
		setupProjects(config);
		Control.setup();
	}
	function setupProjects(config) {
		var $list = $("#configProjectList").html("");
		var $templ = $("#templ_project");
		
		var querys = config.redmineProjectId;
		var names = config.redmineProjectName;
		if (querys == null) return;
		
		for (var i=0; i<querys.length; i++) {
			var $row = $templ.clone();
			$row.find("input[name='redmineProjectName']").val(names[i]);
			$row.find("input[name='redmineProjectId']").val(querys[i]);
			$list.append($row);
		}
	}
	
	Class.setup = setup;

	function getValues($inputs) {
		if ($inputs.length <= 1) return $inputs.val();
		var list = [];
		for (var i=0; i<$inputs.length; i++) {
			var $input = $($inputs[i]);
			list.push($input.val());
		}
		return list;
	}
	function setValues($inputs, vals) {
		if ($inputs.length <= 1) return $inputs.val(vals);
		for (var i=0; i<$inputs.length; i++) {
			$($inputs[i]).val(vals[i]);
		}
	}
	

})(Config);


