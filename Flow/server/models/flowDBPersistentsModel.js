var mongoose = require('mongoose');

var FlowDBPersistentsSchema = new mongoose.Schema({
	publicBuildingRefs: Array,
  imageCounter: Number,
  buildingCounter: Number
});

module.exports = mongoose.model('FlowDBPersistents', FlowDBPersistentsSchema);
