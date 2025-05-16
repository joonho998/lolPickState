$(document).ready(function () {
	const roomId = $("#roomId").val();
    const version = "15.10.1"; // 최신 버전으로 업데이트
    const lang = "ko_KR";
    const champDataUrl = `https://ddragon.leagueoflegends.com/cdn/${version}/data/${lang}/champion.json`;
    const champImageUrl = `https://ddragon.leagueoflegends.com/cdn/${version}/img/champion/`;
    const champFullImageUrl = `http://ddragon.leagueoflegends.com/cdn/img/champion/splash/`
    
    let timerInterval = null; // 타이머 인터벌 저장
    let timerStarted = false; // 타이머가 시작되었는지 여부
    
    let allChampions = {};
    let positionMap = {};
    let currentRole = "ALL"; // 선택된 라인 필터 (기본값: 전체)
    let pickState = {
	    phase: "BAN",  // BAN → PICK
	    order: [
	      "blue-ban-1", "red-ban-1", "blue-ban-2", "red-ban-2", "blue-ban-3", "red-ban-3",
	      "blue-pick-1", "red-pick-1", "red-pick-2", "blue-pick-2", "blue-pick-3", "red-pick-3",
	      "red-ban-4", "blue-ban-4", "red-ban-5", "blue-ban-5",
	      "red-pick-4", "blue-pick-4", "blue-pick-5", "red-pick-5"
	    ],
	    index: 0,
	    selected: [],
	    timeLeft: 30,  // 30초 타이머
    };
    $.ajax({
    	url: "/simulations/pickList",
    	type: "POST",
    	data: {simulationId : $("#simulationId").val()},
    	success: function (res) {
    		pickState.selected.push(...res.split(",").map(s => s.trim()));
    		renderChampionList(Object.keys(allChampions));
    	},
    	error: function () {
    		alert("서버 에러");
    	}
    });
    
    let socket = new WebSocket("wss://lolpickstate-1.onrender.com/ws/draft?roomId=" + encodeURIComponent(roomId));

	socket.onmessage = (event) => {
		console.log("📨 Message from server:", event.data);
		const msg = JSON.parse(event.data);
		
		if (msg.type === "select") {
			const slot = msg.slot;
			const champId = msg.championId;
			var imgUrl = "";
		    if(slot.includes("pick")){
		    	imgUrl = champFullImageUrl + champId +"_0.jpg"
				$(`#${name}`).text(selectedChampionId);
		    }else{
		    	imgUrl = champImageUrl + allChampions[champId].image.full;
		    }
			$(`#${slot}`).attr("src", imgUrl).attr("alt", champId).attr("title", champId);
			pickState.selected.push(champId); // 중복 방지
			advancePick();
			pickState.timeLeft = 30;
			
			renderChampionList(Object.keys(allChampions)); // 갱신
		}
		
		if(msg.type === "start") {
		    console.log("🚀 시작 버튼 클릭 - 타이머 시작");
		    $("#start-button").hide(); // 시작 버튼 숨김
		    startTimer();              // 타이머 시작
		    timerStarted = true;
		  }
	};

	socket.onopen = () => {
		console.log("✅ WebSocket connected");
		socket.send(JSON.stringify({ type: "hello", content: "world" }));
	};
	
	socket.onerror = (e) => {
	    console.error("❌ WebSocket error", e);
	};

	socket.onclose = () => {
	    console.log("🔌 WebSocket closed");
	};
	
	$("#start-button").on("click", function () {
		  // 시작 메시지 WebSocket으로 전송
		  socket.send(JSON.stringify({ type: "start" }));
		  // 버튼은 서버 응답에서 숨겨지므로 여기선 안 숨김
	});
  //현재 슬롯 가져오기
  function getCurrentSlot() {
    return pickState.order[pickState.index];
  }
  
  //픽 단계 진행
  function advancePick() {
    pickState.index++;
    // 벤과 픽 상태 전환 (index 값을 기준으로)
    if (pickState.index >= 0 && pickState.index <= 5) {
      pickState.phase = "BAN"; // 벤 단계
    } else if (pickState.index >= 6 && pickState.index <= 11) {
      pickState.phase = "PICK"; // 픽 단계
    } else if (pickState.index >= 12 && pickState.index <= 15) {
      pickState.phase = "BAN"; // 벤 단계
    } else if (pickState.index >= 16) {
      pickState.phase = "PICK"; // 픽 단계
    }
    
    const currentSlot = getCurrentSlot() || "끝";
    $("#current-slot").text(`${pickState.phase} 중: ${currentSlot}`);

    if (pickState.index >= pickState.order.length) {
      clearInterval(timerInterval);
      $("#timer").text("완료");
      $("#start-button").show();  // 재시작 가능하게
    }
  }

  // 챔피언 목록
  $.getJSON(champDataUrl, function (data) {
    allChampions = data.data;
    renderChampionList(Object.keys(allChampions)); // 초기 렌더링
  });

  // 포지션 데이터
  $.getJSON("/position-champions.json", function (data) {
    positionMap = data;
  });

  // 필터 이벤트
  $("#role-filter").on("click", "button", function () {
    const role = $(this).data("role");
    currentRole = role; // 현재 선택된 라인 필터를 기억합니다.
    console.log("🔎 선택된 역할:", role);
    console.log("📌 해당 역할의 챔피언 목록:", positionMap[role]);
    if (role === "ALL") {
      renderChampionList(Object.keys(allChampions));
    } else {
      const filtered = positionMap[role] || [];
      renderChampionList(filtered);
    }
  });
  
  //챔피언 클릭 시 '선택' 버튼 표시
  let selectedChampionId = null;
  
  //챔피언 클릭
  $("#champion-list").on("click", "img", function () {
	    const champId = $(this).data("id");

	    // 이미 선택된 챔피언은 클릭 불가
	    if (pickState.selected.includes(champId)) {
	      // 선택된 챔피언은 클릭을 막고, 회색 상태 유지
	      return;
	    }

	    // 선택된 챔피언 ID 기록
	    selectedChampionId = champId;

	    // 선택된 이미지를 시각적으로 표시 (선택된 이미지에 'selected' 클래스 추가)
	    $(".champion-icon").removeClass("selected");
	    $(this).addClass("selected");
  });
  
  // '선택' 버튼 클릭 시 챔피언 픽
  $("#select-button").on("click", function () {
    if (!selectedChampionId) return; // 선택된 챔피언이 없으면 무시

    const slot = getCurrentSlot(); //blue-pick-1
    const name = slot.replace("pick", "name");
    if (!slot) return;
    var imgUrl = "";
    if(slot.includes("pick")){
    	imgUrl = champFullImageUrl + selectedChampionId +"_0.jpg"
		$(`#${name}`).text(selectedChampionId);
    }else{
    	imgUrl = champImageUrl + allChampions[selectedChampionId].image.full;
    }
    // 이미지 설정
    $(`#${slot}`).attr("src", imgUrl).attr("alt", selectedChampionId).attr("title", selectedChampionId);
    pickState.selected.push(selectedChampionId);
    advancePick();
    pickState.timeLeft = 30; // 30초 리셋
    
    // 다른 클라이언트에게도 전송
     socket.send(JSON.stringify({
         type: "select",
         slot: slot,
         championId: selectedChampionId
     }));
    
    selectedChampionId = null;  // 선택된 챔피언 초기화
    renderChampionList(Object.keys(allChampions)); // 선택 상태를 반영한 챔피언 목록 렌더링
  });
  
  
  // 타이머 감소 함수 (1초씩 감소)
  function decreaseTimer() {
    if (pickState.timeLeft > 0) {
      pickState.timeLeft--;
      updateTimerDisplay(); // UI에 타이머 업데이트
    }

    if (pickState.timeLeft === 0) {
      handleTimeOut(); // 타이머가 0이 되었을 때 처리
    }
  }

  // 타이머를 화면에 업데이트
  function updateTimerDisplay() {
    $("#timer").text(pickState.timeLeft); // #timer에 남은 시간을 업데이트
  }

  // 타이머가 0초가 되었을 때의 처리 (픽 또는 밴 없이 넘어가도록)
  function handleTimeOut() {
    const slot = getCurrentSlot();
    const phase = pickState.phase;

    // 벤픽에서 아무 것도 선택되지 않으면 🚫 이미지 넣기
    if (phase === "BAN") {
      if (!pickState.selected.includes(slot)) {
    	  $(`#${slot}`).attr("src", "https://cdn-icons-png.flaticon.com/512/1828/1828843.png"); // 금지 이미지
      }
    } else if (phase === "PICK") {
    	// 선택 안된 경우 랜덤으로 픽
        if (!pickState.selected.includes(slot)) {
          randomPick(slot); // 랜덤으로 픽 수행
          // 랜덤 픽 후 선택버튼을 눌러서 자동으로 픽 진행
          $("#select-button").click(); // '선택' 버튼을 자동으로 클릭
        }
    }

    advancePick(); // 순서 진행
    pickState.timeLeft = 30; // 30초 리셋
  }

  // 랜덤으로 픽 수행
  function randomPick(slot) {
	  const allChampionIds = Object.keys(allChampions);  // 모든 챔피언 아이디 리스트
	  const randomIndex = Math.floor(Math.random() * allChampionIds.length);  // 랜덤 인덱스
	  const randomChampionId = allChampionIds[randomIndex];  // 랜덤 챔피언 ID
	  const name = slot.replace("pick", "name");
	  // 랜덤 챔피언의 이미지 URL 가져오기
	  var imgUrl = champFullImageUrl + randomChampionId +"_0.jpg"
	  $(`#${name}`).text(selectedChampionId);
	  // 해당 슬롯에 랜덤 챔피언 이미지 설정
	  $(`#${slot}`).attr("src", imgUrl).attr("alt", randomChampionId).attr("title", allChampions[randomChampionId].name);

	  // 선택된 챔피언 ID를 selected 목록에 추가
	  pickState.selected.push(randomChampionId);
  }
  
  // 챔피언 목록 렌더링
  function renderChampionList(championIds) {
    $("#champion-list").empty();
    const filteredChampions = currentRole === "ALL" ? championIds : championIds.filter(id => positionMap[currentRole].includes(id));
    // 챔피언 이름순으로 정렬
    filteredChampions.forEach(id => {
      if (allChampions[id]) {
        const champ = allChampions[id];
        const imgUrl = champImageUrl + champ.image.full;
        const isSelected = pickState.selected.includes(id);  // 이미 선택된 챔피언인지 확인
        const isDisabled = pickState.selected.includes(id); // 이미 선택된 챔피언은 비활성화
        const champImg = `<img 
            src="${imgUrl}" 
            alt="${champ.name}" 
            title="${champ.name}" 
            data-id="${id}"
            class="champion-icon ${isSelected ? 'selected' : ''} ${isDisabled ? 'disabled' : ''}"
            style="${isDisabled ? 'opacity: 0.5; cursor: not-allowed;' : ''}"
        />`;
        $("#champion-list").append(champImg);
      }
    });
  }

  // 픽/벤 창 초기화
//  for (let i = 1; i <= 5; i++) {
//    $("#blue-bans").append(`<img id="blue-ban-${i}" />`);
//    $("#red-bans").append(`<img id="red-ban-${i}" />`);
//    $("#blue-picks").append(`<img id="blue-pick-${i}" />`);
//    $("#blue-picks").append(`<div class="champion-name" id="blue-name-${i}" /></div>`);
//    $("#red-picks").append(`<img id="red-pick-${i}" />`);
//    $("#red-picks").append(`<div class="champion-name" id="red-name-${i}" /></div>`);
//  }
  
  $("#save-draft-btn").on("click", function () {
	  const draftData = {
	          roomId: roomId,
	          blueBans: [],
	          redBans: [],
	          bluePicks: [],
	          redPicks: []
	      };
	  
	      // 벤픽 데이터를 배열로 수집
	  for (let i = 1; i <= 5; i++) {
		  
	      draftData.blueBans.push($("#blue-ban-" + i).attr("alt") || "");
	      draftData.redBans.push($("#red-ban-" + i).attr("alt") || "");
	      draftData.bluePicks.push($("#blue-pick-" + i).attr("alt") || "");
	      draftData.redPicks.push($("#red-pick-" + i).attr("alt") || "");
	  }
	  console.log($("#simulationId").val());
	  console.log($("#totalSets").val());
	  // 문자열로 변환 (예: "Azir, Bard, Braum, , ")
      const payload = {
          roomId: draftData.roomId,
          simulationId: $("#simulationId").val(),
          totalSets: $("#totalSets").val(),
          blueBans: draftData.blueBans.join(", "),
          redBans: draftData.redBans.join(", "),
          bluePicks: draftData.bluePicks.join(", "),
          redPicks: draftData.redPicks.join(", ")
      };

      // 서버에 전송
      $.ajax({
          url: "/simulations/save",
          type: "POST",
          contentType: "application/json",
          data: JSON.stringify(payload),
          success: function (res) {
              alert("저장 완료!");
              window.location.href = '/simulations';
          },
          error: function () {
              alert("저장 실패!");
          }
      });
	});
  function startTimer() {
	  if (timerInterval) clearInterval(timerInterval); // 기존 인터벌 제거
	  pickState.timeLeft = 30; // 30초 초기화
	  updateTimerDisplay();
	  timerInterval = setInterval(() => {
	    decreaseTimer();
	  }, 1000);
  }
  
  const modal = document.getElementById("info-modal");
  const closeBtn = document.querySelector(".close-button");

  // 모달 열기
  modal.style.display = "block";

  // 닫기 버튼 클릭 시 모달 닫기
  closeBtn.onclick = function() {
    modal.style.display = "none";
  };

  // 모달 바깥 클릭 시 모달 닫기
  window.onclick = function(event) {
    if (event.target === modal) {
      modal.style.display = "none";
    }
  };
});
