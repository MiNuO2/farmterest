/* 팜터레스트 클라이언트 스크립트 (바닐라 JS, DOM 제어) */
(function () {
  "use strict";

  document.addEventListener("DOMContentLoaded", function () {

    /* 1) 필터 아코디언 토글 (모바일/데스크톱 공통) */
    document.querySelectorAll(".filter-head").forEach(function (head) {
      head.addEventListener("click", function (e) {
        if (e.target.closest(".help-btn")) return;   // 도움말 버튼 클릭은 접힘 토글에서 제외
        head.closest(".filter-group").classList.toggle("collapsed");
      });
    });

    /* 1-b) 품질 지표 도움말 모달 (클릭하여 열기 / 배경·X·Esc 로 닫기) */
    function qhClose() {
      var open = document.querySelector(".qh-modal.open");
      if (open) { open.classList.remove("open"); document.body.classList.remove("qh-lock"); }
    }
    document.addEventListener("click", function (e) {
      var trig = e.target.closest("[data-help]");
      if (trig) {
        e.preventDefault();
        var modal = document.getElementById("qh-" + trig.getAttribute("data-help"));
        if (modal) { qhClose(); modal.classList.add("open"); document.body.classList.add("qh-lock"); }
        return;
      }
      if (e.target.closest("[data-qh-close]") || e.target.classList.contains("qh-modal")) { qhClose(); }
    });
    document.addEventListener("keydown", function (e) {
      if (e.key === "Escape") qhClose();
    });

    /* 모바일에서는 필터 그룹 기본 접힘 */
    if (window.matchMedia("(max-width: 900px)").matches) {
      document.querySelectorAll(".filter-group").forEach(function (g, i) {
        if (i > 0) g.classList.add("collapsed");
      });
    }

    /* 2) 품질 게이지 채우기 (data-val = 0~100) */
    requestAnimationFrame(function () {
      document.querySelectorAll(".spec-gauge i").forEach(function (bar) {
        var v = parseFloat(bar.getAttribute("data-val")) || 0;
        bar.style.width = Math.max(0, Math.min(100, v)) + "%";
      });
    });

    /* 3) 수량 스테퍼 */
    document.querySelectorAll(".qty").forEach(function (box) {
      var input = box.querySelector("input");
      box.querySelectorAll("button").forEach(function (btn) {
        btn.addEventListener("click", function () {
          var step = btn.dataset.step === "down" ? -1 : 1;
          var val = Math.max(1, (parseInt(input.value, 10) || 1) + step);
          input.value = val;
          input.dispatchEvent(new Event("change", { bubbles: true }));
        });
      });
    });

    /* 4) 검색 예시 칩 → 검색창 채우고 제출 */
    document.querySelectorAll("[data-example]").forEach(function (chip) {
      chip.addEventListener("click", function (e) {
        var form = document.querySelector(".ai-search form") || document.querySelector(".nav-search");
        if (form) {
          var input = form.querySelector("input[name='q']");
          if (input) { e.preventDefault(); input.value = chip.dataset.example; form.submit(); }
        }
      });
    });

    /* 5) 실시간 인기 검색어 — 끊김 없이 무한 반복되는 연속 세로 스크롤
          (목록을 한 벌 복제해 이어 붙이고, 한 바퀴 끝나면 자동으로 처음으로 되돌아가 반복) */
    document.querySelectorAll(".ns-ticker-roll").forEach(function (roll) {
      var items = Array.prototype.slice.call(roll.children);
      if (!items.length) return;

      function start() {
        var lineH = items[0].getBoundingClientRect().height;
        if (!lineH) { setTimeout(start, 300); return; }   // 숨겨져 있으면 보일 때까지 대기

        // roll(overflow:hidden 뷰포트) 안에 '트랙'을 만들어 spans를 옮기고, 한 벌 복제해 이어붙인다.
        // 트랙만 위로 이동 → 뷰포트는 고정, 콘텐츠만 스크롤. 복제본 시작점이 0과 같아 끊김 없이 순환.
        var track = document.createElement("div");
        track.className = "ns-ticker-track";
        items.forEach(function (el) { track.appendChild(el); });               // 원본 이동
        items.forEach(function (el) { track.appendChild(el.cloneNode(true)); }); // 복제 추가
        roll.appendChild(track);

        var shift = lineH * items.length;                 // 한 목록 높이(px)
        var SECONDS_PER_ITEM = 1.25;                       // 항목당 약 1.25초 → 8개면 한 바퀴 ≈ 10초
        var duration = items.length * SECONDS_PER_ITEM;

        track.style.setProperty("--ns-shift", "-" + shift + "px");
        track.style.animation = "nsRoll " + duration + "s linear infinite";
      }
      start();
    });

    /* 입력 중이거나 포커스되면 티커를 숨겨 입력을 방해하지 않음 */
    (function () {
      var input = document.querySelector(".nav-search input[name='q']");
      var ticker = document.querySelector(".ns-ticker");
      if (!input || !ticker) return;
      var upd = function () {
        ticker.style.display = (input.value || document.activeElement === input) ? "none" : "flex";
      };
      input.addEventListener("focus", upd);
      input.addEventListener("blur", upd);
      input.addEventListener("input", upd);
      upd();
    })();

  });
})();
