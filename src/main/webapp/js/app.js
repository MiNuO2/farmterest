/* 팜터레스트 클라이언트 스크립트 (바닐라 JS, DOM 제어) */
(function () {
  "use strict";

  document.addEventListener("DOMContentLoaded", function () {

    /* 1) 필터 아코디언 토글 (모바일/데스크톱 공통) */
    document.querySelectorAll(".filter-head").forEach(function (head) {
      head.addEventListener("click", function () {
        head.closest(".filter-group").classList.toggle("collapsed");
      });
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

    /* 5) 실시간 인기 검색어 롤링 (아래 → 위로 밀어 올리며 등장) */
    document.querySelectorAll(".ns-ticker-roll").forEach(function (roll) {
      var items = roll.children;
      if (items.length < 2) return;
      var h = items[0].getBoundingClientRect().height;
      var idx = 0;
      var ease = "transform .5s cubic-bezier(.4,0,.2,1)";
      roll.style.transition = ease;
      setInterval(function () {
        idx++;
        roll.style.transform = "translateY(-" + (idx * h) + "px)";
        if (idx >= items.length - 1) {
          // 마지막(첫 항목 복제본)에 도달하면 끊김 없이 처음으로 리셋
          setTimeout(function () {
            roll.style.transition = "none";
            roll.style.transform = "translateY(0)";
            void roll.offsetHeight; // 리플로우 강제
            roll.style.transition = ease;
            idx = 0;
          }, 520);
        }
      }, 2300);
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
