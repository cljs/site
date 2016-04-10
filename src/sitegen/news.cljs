(ns sitegen.news)

(def posts
  [{:title "Introducing ClojureScript",
    :external_name "Clojure.com/blog",
    :external_source
    "http://clojure.com/blog/2011/07/22/introducing-clojurescript.html",
    :author "Stuart Sierra",
    :filename "2011-07-22-introducing-clojurescript.md"}
   {:release_version "0.0-927",
    :title "0.0-927",
    :author "Stuart Sierra",
    :google_group_msg "clojure/L4e8DtzdThY/IHIBBpZ8PrQJ",
    :filename "2012-01-20-r927.md"}
   {:release_version "0.0-971",
    :title "0.0-971",
    :author "Stuart Sierra",
    :google_group_msg "clojure/2k0zyWnTs0M/oKGAr9lGxuUJ",
    :filename "2012-01-27-r971.md"}
   {:release_version "0.0-1211",
    :title "0.0-1211",
    :author "Stuart Sierra",
    :google_group_msg "clojure/ms8HWwuqTKY/sgyZm36ITKkJ",
    :filename "2012-05-10-r1211.md"}
   {:release_version "0.0-1236",
    :title "0.0-1236",
    :author "Stuart Sierra",
    :google_group_msg "clojure/OakrMSmQZ1U/ZUW6XvERQGcJ",
    :filename "2012-05-16-r1236.md"}
   {:release_version "0.0-1443",
    :title "0.0-1443",
    :author "Stuart Sierra",
    :google_group_msg "clojure/g2DE1Zz_0e4/yN-ISWHR6ccJ",
    :filename "2012-07-06-r1443.md"}
   {:release_version "0.0-1503",
    :title "0.0-1503",
    :author "Stuart Sierra",
    :google_group_msg "clojure/gI9dbCaDX_8/TC6JuloBapIJ",
    :filename "2012-10-14-r1503.md"}
   {:release_version "0.0-1513",
    :title "0.0-1513",
    :author "Stuart Sierra",
    :google_group_msg "clojure/19gbJ4r3NBw/c2BOlZzMaSoJ",
    :filename "2012-10-19-r1513.md"}
   {:release_version "0.0-1535",
    :title "0.0-1535 with G.Closure 0.0-2029",
    :author "Stuart Sierra",
    :google_group_msg "clojure/kzzBoCZiAx0/dXx5DR7vrV4J",
    :filename "2012-11-09-r1535.md"}
   {:release_version "0.0-1586",
    :title "0.0-1586",
    :author "Stuart Sierra",
    :google_group_msg "clojure/hdYOKhiC9Ag/jX7zkEaCkesJ",
    :filename "2013-02-16-r1586.md"}
   {:release_version "0.0-1798",
    :title "0.0-1798",
    :author "David Nolen",
    :google_group_msg "clojure/9mtwpYTx6RI/MHacS_AdinEJ",
    :filename "2013-05-03-r1798.md"}
   {:release_version "0.0-1847",
    :title "0.0-1847",
    :author "Stuart Sierra",
    :google_group_msg "clojure/rvw_hfBCOWc/a8XEI8McjM0J",
    :filename "2013-07-24-r1847.md"}
   {:release_version "0.0-1859",
    :title "0.0-1859",
    :author "David Nolen",
    :google_group_msg "clojurescript/5jjIZL52UQQ/YBx97JnhP28J",
    :filename "2013-08-21-r1859.md"}
   {:release_version "0.0-1877",
    :title "0.0-1877 - Breaking change",
    :author "David Nolen",
    :google_group_msg "clojurescript/qnRQdmu5yT8/Q8h8_wNjn9IJ",
    :filename "2013-09-08-r1877.md"}
   {:release_version "0.0-1885",
    :title "0.0-1885 - Source maps!",
    :author "David Nolen",
    :google_group_msg "clojurescript/N31hDu83zeI/dr4Iwy96ScEJ",
    :filename "2013-09-15-r1885-source-maps.md"}
   {:release_version "0.0-1909",
    :title "0.0-1909",
    :author "David Nolen",
    :google_group_msg "clojurescript/UsHggHPo-DM/RzqJuW8uy8wJ",
    :filename "2013-09-27-r1909.md"}
   {:release_version "0.0-1913",
    :title "0.0-1913",
    :author "David Nolen",
    :google_group_msg "clojurescript/leynEQDaoyk/xVdm5XpG6nEJ",
    :filename "2013-10-04-r1913.md"}
   {:release_version "0.0-1933",
    :title "0.0-1933",
    :author "David Nolen",
    :google_group_msg "clojurescript/O0Pm_CgQjuE/Vgo_ldFxwscJ",
    :filename "2013-10-11-r1933.md"}
   {:release_version "0.0-1934",
    :title "0.0-1934",
    :author "David Nolen",
    :google_group_msg "clojurescript/vmD-L5H-_4Y/7RHrGO5yGUcJ",
    :filename "2013-10-11-r1934.md"}
   {:release_version "0.0-1978",
    :title "0.0-1978",
    :author "David Nolen",
    :google_group_msg "clojurescript/0ZI5JEUgrG8/71yeTzZi_xEJ",
    :filename "2013-10-27-r1978.md"}
   {:release_version "0.0-2014",
    :title
    "0.0-2014 - source maps, incremental compilation, and internal changes",
    :author "David Nolen",
    :google_group_msg "clojurescript/isf7k35pThA/CXyJr4v4seYJ",
    :filename
    "2013-11-06-r2014-source-maps-incremental-compilation-and-internal-changes.md"}
   {:release_version "0.0-2024",
    :title "0.0-2024",
    :author "David Nolen",
    :google_group_msg "clojurescript/HWjBRlZgCLw/9eF24GN4bRAJ",
    :filename "2013-11-08-r2024.md"}
   {:release_version "0.0-2030",
    :title "0.0-2030 - core.async compatibility",
    :author "David Nolen",
    :google_group_msg "clojurescript/itdtHoWWAGs/8TdVgalJexgJ",
    :filename "2013-11-08-r2030-core.async-compatibility.md"}
   {:release_version "0.0-2060",
    :title "0.0-2060",
    :author "David Nolen",
    :google_group_msg "clojurescript/LGmAV1Xxm30/0Jy7cZRsJj4J",
    :filename "2013-11-21-r2060.md"}
   {:release_version "0.0-2067",
    :title "0.0-2067 - regressions, type inference & numeric checks",
    :author "David Nolen",
    :google_group_msg "clojurescript/CcIFD5q9kqg/TNJMLdLRCkAJ",
    :filename
    "2013-11-22-r2067-regressions-type-inference-and-numeric-checks.md"}
   {:release_version "0.0-2120",
    :title "0.0-2120",
    :author "David Nolen",
    :google_group_msg "clojurescript/mUVbtdnAvHA/LisdVWyAJKwJ",
    :filename "2013-12-13-r2120.md"}
   {:release_version "0.0-2127",
    :title "0.0-2127",
    :author "David Nolen",
    :google_group_msg "clojurescript/0nxFc9xDmDw/juGJaPKRiNEJ",
    :filename "2013-12-20-r2127.md"}
   {:release_version "0.0-2134",
    :title "0.0-2134",
    :author "David Nolen",
    :google_group_msg "clojurescript/NiUwST-8PO4/FowOiv6CfVAJ",
    :filename "2013-12-30-r2134.md"}
   {:release_version "0.0-2138",
    :title "0.0-2138",
    :author "David Nolen",
    :google_group_msg "clojurescript/z_qvCeYmMgo/e2FiHturFgcJ",
    :filename "2013-12-30-r2138.md"}
   {:release_version "0.0-2156",
    :title "0.0-2156",
    :author "David Nolen",
    :google_group_msg "clojurescript/bCNXDBrDVEs/i7qwfSv1fdsJ",
    :filename "2014-01-28-r2156.md"}
   {:release_version "0.0-2173",
    :title "0.0-2173",
    :author "David Nolen",
    :google_group_msg "clojurescript/jW8IN9jySYo/DBPrDLLQi1sJ",
    :filename "2014-02-21-r2173.md"}
   {:release_version "0.0-2197",
    :title "0.0-2197",
    :author "David Nolen",
    :google_group_msg "clojurescript/UKgf-rC1Y_8/5yvZ2IHOWkwJ",
    :filename "2014-03-26-r2197.md"}
   {:release_version "0.0-2199",
    :title "0.0-2199",
    :author "David Nolen",
    :google_group_msg "clojurescript/SChRa6iUCL8/-tcxyadZyYMJ",
    :filename "2014-04-01-r2199.md"}
   {:release_version "0.0-2202",
    :title "0.0-2202",
    :author "David Nolen",
    :google_group_msg "clojurescript/Eoj3zxpM7zY/rxw4Y5CkOV8J",
    :filename "2014-04-02-r2202.md"}
   {:release_version "0.0-2227",
    :title "0.0-2227",
    :author "David Nolen",
    :google_group_msg "clojurescript/QOW4HbctqE8/IlEw_bxqH9wJ",
    :filename "2014-05-22-r2227.md"}
   {:release_version "0.0-2234",
    :title "0.0-2234",
    :author "David Nolen",
    :google_group_msg "clojurescript/KN20FwoyoZ8/BZlGWNTnQxUJ",
    :filename "2014-06-13-r2234.md"}
   {:release_version "0.0-2261",
    :title "0.0-2261",
    :author "David Nolen",
    :google_group_msg "clojurescript/lHVUNTbdGBU/_blg-TDGBtIJ",
    :filename "2014-07-01-r2261.md"}
   {:release_version "0.0-2268",
    :title "0.0-2268",
    :author "David Nolen",
    :google_group_msg "clojurescript/i_w6fkfNW3U/mNYBJW9QPbcJ",
    :filename "2014-07-06-r2268.md"}
   {:release_version "0.0-2277",
    :title "0.0-2277",
    :author "David Nolen",
    :google_group_msg "clojurescript/AuHJVXVdf_U/zFFiUacBR7gJ",
    :filename "2014-07-25-r2277.md"}
   {:release_version "0.0-2280",
    :title "0.0-2280",
    :author "David Nolen",
    :google_group_msg "clojurescript/shiWkRKrZiw/imdrU2qULFoJ",
    :filename "2014-08-01-r2280.md"}
   {:release_version "0.0-2301",
    :title "0.0-2301 - Transducers!",
    :author "David Nolen",
    :google_group_msg "clojurescript/ghpbnZKjx3w/enfypPKOR2YJ",
    :filename "2014-08-07-r2301-transducers.md"}
   {:release_version "0.0-2311",
    :title "0.0-2311",
    :author "David Nolen",
    :google_group_msg "clojurescript/1IVqbqKlJLw/uMaQ_ocx0TYJ",
    :filename "2014-08-09-r2311.md"}
   {:release_version "0.0-2322",
    :title "0.0-2322 - Safari Hashing Fix",
    :author "David Nolen",
    :google_group_msg "clojurescript/n0sfFX09Vbw/9Icme54Z-X0J",
    :filename "2014-08-27-r2322-safari-hashing-fix.md"}
   {:release_version "0.0-2341",
    :title "0.0-2341 - Improved Analysis & Transducers",
    :author "David Nolen",
    :google_group_msg "clojurescript/kBo9pSNScm8/-Ovdkc6ZSwMJ",
    :filename "2014-09-18-r2341-improved-analysis-and-transducers.md"}
   {:release_version "0.0-2356",
    :title "0.0-2356",
    :author "David Nolen",
    :google_group_msg "clojurescript/LxGSTCfAUZU/Wt0AicRptuAJ",
    :filename "2014-09-26-r2356.md"}
   {:release_version "0.0-2371",
    :title "0.0-2371",
    :author "David Nolen",
    :google_group_msg "clojurescript/tigdfow0w9A/kb7UC-iZmI0J",
    :filename "2014-10-10-r2371.md"}
   {:release_version "0.0-2411",
    :title "0.0-2411",
    :author "David Nolen",
    :google_group_msg "clojurescript/M7GQx25XGU4/jhsptdOMLVgJ",
    :filename "2014-12-05-r2411.md"}
   {:release_version "0.0-2496",
    :title "0.0-2496 - cljs.test - a clojure.test port",
    :author "David Nolen",
    :google_group_msg "clojurescript/gnCl0CySSk8/_bA95j80KHQJ",
    :filename "2014-12-17-r2496-cljs.test.md"}
   {:release_version "0.0-2505",
    :title "0.0-2505",
    :author "David Nolen",
    :google_group_msg "clojurescript/3GWuFUr5z5o/62gUT6M0xqEJ",
    :filename "2014-12-21-r2505.md"}
   {:release_version "0.0-2644",
    :title "0.0-2644 - enhanced REPLs",
    :author "David Nolen",
    :google_group_msg "clojurescript/mRAED4TFlCM/VMi-AOQBzFQJ",
    :filename "2015-01-02-r2644-enhanced-repls.md"}
   {:release_version "1.7.228",
    :title "1.7.170 - parallel builds!",
    :author "David Nolen",
    :google_group_msg "clojurescript/1R8YtC99xp8/QlcAoSSECwAJ",
    :filename "2015-01-08-r1.7.228-parallel-builds.md"}
   {:release_version "0.0-2719",
    :title "0.0-2719 - JavaScript Dependencies",
    :author "David Nolen",
    :google_group_msg "clojurescript/pJ_EYHkYAUs/mLi8XfiQxZsJ",
    :filename "2015-01-24-r2719-javascript-dependencies.md"}
   {:release_version "0.0-2740",
    :title "0.0-2740 - Windows",
    :author "David Nolen",
    :google_group_msg "clojurescript/vFw5gOhSf0M/FN-e_ElcYbQJ",
    :filename "2015-01-28-r2740-windows.md"}
   {:release_version "0.0-2755",
    :title
    "r0.0-2755 - Browser REPL, macros, and incremental compilation",
    :author "David Nolen",
    :google_group_msg "clojurescript/pJ_EYHkYAUs/mLi8XfiQxZsJ",
    :filename
    "2015-02-01-r2755-browser-repl-macros-and-incremental-compilation.md"}
   {:release_version "0.0-2814",
    :title "0.0-2814 - Nashorn REPL, async testing, and much more",
    :author "David Nolen",
    :google_group_msg "clojurescript/KTOhX-QvpRo/j9-st6WUnI8J",
    :filename
    "2015-02-09-r2814-nashorn-repl-async-testing-and-much-more.md"}
   {:release_version "0.0-2843",
    :title "0.0-2843 - Node, Node, Node",
    :author "David Nolen",
    :google_group_msg "clojurescript/9ZrGYIhRzqM/-6GM31HTz7wJ",
    :filename "2015-02-12-r2843-node-node-node.md"}
   {:release_version "0.0-2913",
    :title "0.0-2913 - Google Closure Modules, improved nREPL support",
    :author "David Nolen",
    :google_group_msg "clojurescript/n_8WHnlcOGI/1kmATGABVi0J",
    :filename
    "2015-02-21-r2913-google-closure-modules-improved-nrepl-support.md"}
   {:release_version "0.0-3058",
    :title "0.0-3058 - Enhanced REPLs, faster compile times",
    :author "David Nolen",
    :google_group_msg "clojurescript/fdT3f1HxJzM/rCbi7L1AI24J",
    :filename "2015-03-09-r3058-enhanced-repls-faster-compile-times.md"}
   {:release_version "0.0-3115",
    :title "0.0-3115",
    :author "David Nolen",
    :google_group_msg "clojurescript/e3gOEUJlkMc/PATUU2JaBl4J",
    :filename "2015-03-16-r3115.md"}
   {:release_version "0.0-3126",
    :title "0.0-3126 - fix for minor REPL regression",
    :author "David Nolen",
    :google_group_msg "clojurescript/rt7Fc86v1aU/u5G56vi5M6sJ",
    :filename "2015-03-18-r3126-fix-for-minor-repl-regression.md"}
   {:release_version "0.0-3196",
    :title "0.0-3196 - Conditional Reading, REPLs, and Code Motion",
    :author "David Nolen",
    :google_group_msg "clojurescript/pdZVL6gAPio/Jtv7WmuEK9QJ",
    :filename
    "2015-04-10-r3196-conditional-reading-repls-and-code-motion.md"}
   {:release_version "0.0-3211",
    :title "0.0-3211",
    :author "David Nolen",
    :google_group_msg "clojurescript/UhmSvyQVJGg/CWfwqNOub5YJ",
    :filename "2015-04-23-r3211.md"}
   {:release_version "0.0-3255",
    :title
    "r0.0-3255 - Pretty Printer and Latest Closure Compiler/Library",
    :author "David Nolen",
    :google_group_msg "clojurescript/A--qv0JxfO8/FoCzLNQ-D4EJ",
    :filename "2015-05-08-r3255-pretty-printer.md"}
   {:release_version "0.0-3308",
    :title "0.0-3308 - Fixes and Enhancements",
    :author "David Nolen",
    :google_group_msg "clojurescript/kNIKpsFgvyk/y1u_AjChLfYJ",
    :filename "2015-06-01-r3308-fixes-and-enhancements.md"}
   {:release_version "1.7.28",
    :title "1.7.28 - Optional Self Hosting",
    :author "David Nolen",
    :google_group_msg "clojurescript/Z6xD9UthbvQ/gsLMbURGAgAJ",
    :filename "2015-07-31-r1.7.28-optional-self-hosting.md"}
   {:release_version "1.7.48",
    :title "1.7.48",
    :author "David Nolen",
    :google_group_msg "clojurescript/zvZtw7UoyQA/cyP0r6HeAwAJ",
    :filename "2015-08-05-r1.7.48.md"}
   {:release_version "1.7.58",
    :title "1.7.58 (Windows fix)",
    :author "David Nolen",
    :github_pre_release "r1.7.58",
    :filename "2015-08-05-r1.7.58-windows-fix.md"}
   {:release_version "1.7.122",
    :title "1.7.122 (minor fixes)",
    :author "David Nolen",
    :github_pre_release "r1.7.122",
    :filename "2015-08-29-r1.7.122-minor-fixes.md"}
   {:release_version "1.7.145",
    :title "1.7.145",
    :author "David Nolen",
    :google_group_msg "clojurescript/o7AF4XeksZ4/dlxxQ_p2CQAJ",
    :filename "2015-10-13-r1.7.145.md"}
   {:release_version "1.7.170",
    :title "1.7.170 - Enhanced Build Pipeline",
    :author "David Nolen",
    :google_group_msg "clojurescript/AiCARjGT2Mg/J2TdxtHgBAAJ",
    :filename "2015-11-06-r1.7.170-enhanced-build-pipeline.md"}
   {:release_version "1.8.34",
    :title "1.8.34",
    :author "David Nolen",
    :google_group_msg "clojurescript/J2d_WNHrDr4/degpx-WyDAAJ",
    :filename "2016-03-18-r1.8.34.md"}
   {:release_version "1.8.40",
    :title "1.8.40",
    :author "David Nolen",
    :google_group_msg "clojurescript/pIcyxxYzIFg/Kktcv9uxDwAJ",
    :filename "2016-03-28-r1.8.40.md"}])
