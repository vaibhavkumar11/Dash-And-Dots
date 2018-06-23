// var play_id_num;
// db.collection("youtube_videos")
//   .get()
//   .then(snapshot => {
//     snapshot.docs.forEach(doc => {
//       play_id_num = doc.data().num;
//     });
//   });

// setInterval(function() {
//   play_id = play_id_num[0];
//   var tag = document.createElement("script");
//   tag.id = "iframe-demo";
//   tag.src = "https://www.youtube.com/iframe_api";
//   var firstScriptTag = document.getElementsByTagName("script")[0];
//   firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);

//   var player;
//   function onYouTubeIframeAPIReady() {
//     console.log("player api loading");
//     player = new YT.Player(play_id_num[0], {
//       events: {
//         onReady: onPlayerReady
//       }
//     });
//   }

//   function onPlayerReady(event) {
//     console.log("video load");
//     event.target.playVideo();
//   }
// }, 3000);
// // var play_id = 1;
var videoRef = db.collection("videos");

videoRef.doc("vid").set({
  channel: "2"
});
// videoRef.doc("extra").set({
//    channel: "1"
//  });

var docRef = db.collection("videos").doc("vid");

docRef
  .get()
  .then(function(doc) {
    if (doc.exists) {
      tup = doc.data().channel;
      // console.log("Document data:", doc.data());
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
          }
        });
      };

      function onPlayerReady(event) {
        console.log("video load");
        event.target.playVideo();
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
      }, 6000);
    } else {
      // doc.data() will be undefined in this case
      console.log("No such document!");
    }
  })
  .catch(function(error) {
    console.log("Error getting document:", error);
  });
