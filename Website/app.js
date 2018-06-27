var videoRef = db.collection("videos");
var tup;
var k = 0;

function videoRunner() {
  var docRef = db.collection("videos").doc("vid");

  docRef
    .get()
    .then(function(doc) {
      if (doc.exists) {
        tup = doc.data().channel;
        console.log("Document data:", tup);
        
        var tag = document.createElement("script");
        tag.id = "iframe-demo";
        tag.src = "https://www.youtube.com/iframe_api";
        var firstScriptTag = document.getElementsByTagName("script")[0];
        firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);
        var player;
        window.onYouTubeIframeAPIReady = function() {
          console.log("player api loading");
          player = new YT.Player(tup, {
            events: {
              onReady: onPlayerReady
              // onStateChange: onPlayerStateChange
            }
          });
        };

        function onPlayerReady(event) {
          console.log("video load");
          if (event.data != 1) event.target.playVideo();
        }

        setTimeout(function() {
          console.log("djsnjd");
          docRef
            .delete()
            .then(function() {
              console.log("Document successfully deleted!");
            })
            .catch(function(error) {
              console.error("Error removing document: ", error);
            });
        }, 5000);
      } else {
        // doc.data() will be undefined in this case
        console.log("No such document!");
      }
    })
    .catch(function(error) {
      console.log("Error getting document:", error);
    });
}

db.collection("videos").onSnapshot(snapshot => {
  let changes = snapshot.docChanges();
  changes.forEach(change => {
    console.log(change.doc.data());
    videoRunner();
    if (change.type == "added" && k != 0) location.reload();
    k++;
  });
});
