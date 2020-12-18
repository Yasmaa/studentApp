const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.nouvelleReclamation = functions.database.ref('claim/{id}').onCreate((snap, context) => {
	
	const recId = snap.key;
	console.log(recId);
	
    const payload = {
		data:{
			recId : recId,
			type : '2'
		}
    };
    return admin.database().ref('token/agent').once('value').then(agentTokens => {
        if(agentTokens.val()){
            console.log('Agent token available');
            const token = Object.values(agentTokens.val());
            return admin.messaging().sendToDevice(token,payload);
        }else{
            console.log('Agent token available');
			return null;
        }
    });
});



exports.changementEtat = functions.database.ref('claim/{id}').onUpdate((change, context) => {
	
	const recId = change.after.key;
	const userId = change.after.val()['userId'];
	const etat = change.after.val()['etat'];
	console.log(recId);
	console.log(userId);
	console.log(etat);
	
    const payload = {
		data:{
			recId : recId,
			type : '1',
			etat : etat
		}
    };
	    return admin.database().ref('token/user/'+userId).once('value').then(userToken => {
        if(userToken.val()){
			console.log(userToken.val());
            console.log('User token available');
            const token = userToken.val();
            return admin.messaging().sendToDevice(token,payload);
        }else{
            console.log('User token available');
			return null;
        }
    });
});