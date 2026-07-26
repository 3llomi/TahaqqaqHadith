package com.devlomi.tahaqqaqhadith

import com.devlomi.tahaqqaqhadith.data.network.parseSearchResponse
import com.devlomi.tahaqqaqhadith.data.parser.HadithHtmlParser
import com.devlomi.tahaqqaqhadith.data.parser.FakeHadithPageParser
import com.devlomi.tahaqqaqhadith.data.model.LegitimacyState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SharedCommonTest {

    @Test
    fun parsesEmbeddedHtmlIntoEntries() {
        val response = parseSearchResponse(sampleJson)
        val result = HadithHtmlParser().parse(query = "بكورها", html = response.ahadith.result)

        assertEquals(2, result.entries.size)
        assertTrue(result.entries.first().hadithText.contains("بورك"))
        assertEquals("أنس بن مالك", result.entries.first().narrator)
        assertEquals("صحيح", result.entries.last().verdict)
    }

    @Test
    fun computesExpectedOverallState() {
        val response = parseSearchResponse(sampleJson)
        val result = HadithHtmlParser().parse(query = "بكورها", html = response.ahadith.result)

        assertEquals(LegitimacyState.NEEDS_REVIEW, result.overallAssessment.state)
        assertTrue(result.overallAssessment.score in 0..100)
    }

    @Test
    fun parsesFakeHadithPageItemsWithOptionalAlternative() {
        val result = FakeHadithPageParser().parse(page = 3, rawContent = fakeHadithPageMarkdown)

        assertEquals(2, result.items.size)
        assertEquals(41, result.items[0].number)
        assertTrue(result.items[0].hadith.contains("البلاء موكل بالمنطق"))
        assertTrue(result.items[0].grade?.contains("لا يصح") == true)
        assertEquals("https://dorar.net/fake-hadith/41?alts=1", result.items[0].sahihAlternativeUrl)

        assertEquals(43, result.items[1].number)
        assertTrue(result.items[1].grade.isNullOrBlank())
        assertTrue(result.items[1].text.contains("الجنة تحت أقدام الأمهات"))
    }

    @Test
    fun parsesFakeHadithRawHtmlArticles() {
        val result = FakeHadithPageParser().parse(page = 3, rawContent = fakeHadithPageHtml)

        assertEquals(2, result.items.size)
        assertEquals(41, result.items[0].number)
        assertTrue(result.items[0].hadith.contains("البلاء موكل بالمنطق"))
        assertTrue(result.items[0].grade?.contains("لا يصح") == true)
        assertEquals("https://dorar.net/fake-hadith/41?alts=1", result.items[0].sahihAlternativeUrl)

        assertEquals(42, result.items[1].number)
        assertTrue(result.items[1].hadith.contains("التمس لأخيك"))
        assertTrue(result.items[1].grade?.contains("الحديث بهذا اللفظ") == true)
    }

    @Test
    fun parsesFakeHadithRawHtmlArticlesFull() {
        val result = FakeHadithPageParser().parse(page = 3, rawContent = fakeHadithPageHtmlFull)

        val hadith41Item = requireNotNull(result.items.firstOrNull { it.number == 41 })
        val hadith43Item = requireNotNull(result.items.firstOrNull { it.number == 43 })
        val hadith36Item = requireNotNull(result.items.firstOrNull { it.number == 36 })

        assertEquals("https://dorar.net/fake-hadith/41?alts=1", hadith41Item.sahihAlternativeUrl)
        assertEquals("https://dorar.net/fake-hadith/43?alts=1", hadith43Item.sahihAlternativeUrl)
        assertEquals("https://dorar.net/fake-hadith/36?alts=1", hadith36Item.sahihAlternativeUrl)
    }

    private val fakeHadithPageHtml = """
        <html><body>
        <article class="border-bottom py-4">
            <h5 class="h5-responsive edit" data-name="hadith" data-type="textarea" data-pk="41">
                41 - حديث: ((البلاء موكل بالمنطق)).
            </h5>
            <div class="d-block mb-2">
                <strong class="px-2">الدرجة:
                    <span class="primary-text-color edit" data-name="degree" data-type="text" data-pk="41">لا يصح، وصحح معناه ابن القيم في «تحفة المودود».</span>
                </strong>
                <strong class="px-2">
                    <span class="text-danger">|</span>
                    <a class="mr-2 text-info" target="_blank" href="https://dorar.net/fake-hadith/41?alts=1">الصحيح البديل</a>
                </strong>
            </div>
            <a href="https://dorar.net/fake-hadith/41" class="btn" title="عرض الحديث"></a>
        </article>

        <article class="border-bottom py-4">
            <h5 class="h5-responsive edit" data-name="hadith" data-type="textarea" data-pk="42">
                42 - حديث: ((التمس لأخيك بِضعًا وسبعين عُذرًا)).
            </h5>
            <div class="d-block mb-2">
                <strong class="px-2">الدرجة:
                    <span class="primary-text-color edit" data-name="degree" data-type="text" data-pk="42">الحديث بهذا اللفظ لم نجدْه.</span>
                </strong>
            </div>
            <a href="https://dorar.net/fake-hadith/42" class="btn" title="عرض الحديث"></a>
        </article>
        </body></html>
    """.trimIndent()

    private val sampleJson = """
        {
          "ahadith": {
            "result": "<div class=\"hadith\">1 - بورك لأمتي في بكورها .</div><div class=\"hadith-info\"><span class=\"info-subtitle\">الراوي:</span> أنس بن مالك <span class=\"info-subtitle\">المحدث:</span> الذهبي <span class=\"info-subtitle\">المصدر:</span> ميزان الاعتدال <span class=\"info-subtitle\">الصفحة أو الرقم:</span> 3/171 <span class=\"info-subtitle\">خلاصة حكم المحدث:</span> ضعيف</div><div class=\"hadith\">2 - بورك لأمتي في بكورها .</div><div class=\"hadith-info\"><span class=\"info-subtitle\">الراوي:</span> عبدالله بن عمر <span class=\"info-subtitle\">المحدث:</span> الألباني <span class=\"info-subtitle\">المصدر:</span> صحيح الجامع <span class=\"info-subtitle\">الصفحة أو الرقم:</span> 2841 <span class=\"info-subtitle\">خلاصة حكم المحدث:</span> صحيح</div><a href=\"https://dorar.net/hadith/search?q=بكورها\">المزيد</a>"
          }
        }
    """.trimIndent()

    private val fakeHadithPageMarkdown = """
        Title: أحاديث منتشرة لا تصح

        Markdown Content:
        ##### 41 - حديث: ((البلاء موكل بالمنطق)).

        **الدرجة: لا يصح، وصحح معناه ابن القيم في تحفة المودود.****|[الصحيح البديل](https://dorar.net/fake-hadith/41?alts=1)**

        [](https://dorar.net/fake-hadith/41)[](https://dorar.net/fake-hadith/41 "عرض الحديث")
        ##### 43 - حديث: ((الجنة تحت أقدام الأمهات)).

         وفي لفظ: ((الجنة تحت أقدام الأمهات، مَنْ شِئن أدخلن، ومَن شِئن أخرجن!))

        [](https://dorar.net/fake-hadith/43)[](https://dorar.net/fake-hadith/43 "عرض الحديث")
    """.trimIndent()

    private val fakeHadithPageHtmlFull = """
        <!DOCTYPE html>
<!-- saved from url=(0036)https://dorar.net/fake-hadith?page=3 -->
<html lang="ar" dir="rtl"><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <!-- Global site tag (gtag.js) - Google Analytics -->
    <!-- Google Analytics 4 -->
    <script async="" src="./أحاديث منتشرة لا تصح_files/gtm.js"></script><script async="" src="./أحاديث منتشرة لا تصح_files/js"></script>
    <script>
        window.dataLayer = window.dataLayer || [];

        function gtag() {
            dataLayer.push(arguments);
        }


        gtag('js', new Date());

        // Get the current URL

        const currentUrl = window.location.href;

        // Create a URL object

        const url = new URL(currentUrl);

        // Get the pathname
        const pathname = url.pathname; // e.g., "/tafseer/2" or "/tafseer"

        // Split the pathname into segments and get the first one after the root
        window.firstPath = pathname.split('/').filter(Boolean)[0]; // "tafseer"
        // Set up the basic config
        gtag('config', 'G-C8X46MR1C2', {
            'categories': [window.firstPath ? window.firstPath : 'home'],
            //'debug_mode': true
        });


        // Function to update UID when available
        function updateGtagUID(uid) {
            if (uid) {
                gtag('set', {
                    'user_id': uid,
                });
                gtag('event', 'page_view', { 'user_id': uid, 'categories': [window.firstPath ? window.firstPath : 'home'] });
            }
            //console.log('Gtag UID updated:', uid);
        }
    </script>

   <script>(function(w,d,s,l,i){w[l]=w[l]||[];w[l].push({'gtm.start':
    new Date().getTime(),event:'gtm.js'});var f=d.getElementsByTagName(s)[0],
    j=d.createElement(s),dl=l!='dataLayer'?'&l='+l:'';j.async=true;j.src=
    'https://www.googletagmanager.com/gtm.js?id='+i+dl;f.parentNode.insertBefore(j,f);
    })(window,document,'script','dataLayer','GTM-TQTFGJFM');</script>
    <!-- End Google Tag Manager -->



    
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta http-equiv="x-ua-compatible" content="ie=edge">

    <meta name="csrf-token" content="EOcTKpAyeE1kTQSpsX7veXihkZfRaDHo5ETuMXOL">
        <title>    أحاديث منتشرة لا تصح
</title>

    <!-- Update canonical tag to use absolute URL and handle alternates properly -->
    
                    <link rel="canonical" href="https://dorar.net/fake-hadith?page=3">
    
    


    <link rel="apple-touch-icon" sizes="57x57" href="https://dorar.net/img/favico.ico/apple-icon-57x57.png">
    <link rel="apple-touch-icon" sizes="60x60" href="https://dorar.net/img/favico.ico/apple-icon-60x60.png">
    <link rel="apple-touch-icon" sizes="72x72" href="https://dorar.net/img/favico.ico/apple-icon-72x72.png">
    <link rel="apple-touch-icon" sizes="76x76" href="https://dorar.net/img/favico.ico/apple-icon-76x76.png">
    <link rel="apple-touch-icon" sizes="114x114" href="https://dorar.net/img/favico.ico/apple-icon-114x114.png">
    <link rel="apple-touch-icon" sizes="120x120" href="https://dorar.net/img/favico.ico/apple-icon-120x120.png">
    <link rel="apple-touch-icon" sizes="144x144" href="https://dorar.net/img/favico.ico/apple-icon-144x144.png">
    <link rel="apple-touch-icon" sizes="152x152" href="https://dorar.net/img/favico.ico/apple-icon-152x152.png">
    <link rel="apple-touch-icon" sizes="180x180" href="https://dorar.net/img/favico.ico/apple-icon-180x180.png">
    <link rel="icon" type="image/png" sizes="192x192" href="https://dorar.net/img/favico.ico/android-icon-192x192.png">
    <link rel="icon" type="image/png" sizes="32x32" href="https://dorar.net/img/favico.ico/favicon-32x32.png">
    <link rel="icon" type="image/png" sizes="96x96" href="https://dorar.net/img/favico.ico/favicon-96x96.png">
    <link rel="icon" type="image/png" sizes="16x16" href="https://dorar.net/img/favico.ico/favicon-16x16.png">
    <link rel="manifest" href="https://dorar.net/img/favico.ico/manifest.json">
    <meta name="msapplication-TileColor" content="#ffffff">
    <meta name="msapplication-TileImage" content="/img/favico.ico/ms-icon-144x144.png">

    <!-- Font Awesome -->
    <link rel="preload" href="https://dorar.net/font/dubai/DubaiW23-Medium.woff2" as="font" type="font/woff2" crossorigin="">
    <link rel="preload" href="https://dorar.net/font/dubai/DubaiW23-Bold.woff2" as="font" type="font/woff2" crossorigin="">
    <link rel="preload" href="https://dorar.net/font/dubai/DubaiW23-Regular.woff2" as="font" type="font/woff2" crossorigin="">
    
    
    <link rel="stylesheet" href="./أحاديث منتشرة لا تصح_files/vendor.bundle.min.css">
    <link href="./أحاديث منتشرة لا تصح_files/main.css" rel="stylesheet">
    
    <!-- Your custom styles (mandatory) -->
    <style>
        @media(max-width: 991px){
            .side-nav {
                position: absolute !important;
            }
            #slide-out {
                display: none !important;
            }
            #slide-out.active {
                display: block !important;
            }
        }
    </style>
    <link rel="preload" as="style" href="./أحاديث منتشرة لا تصح_files/mainapp-AwPMA3mb.css"><link rel="stylesheet" href="./أحاديث منتشرة لا تصح_files/mainapp-AwPMA3mb.css" data-navigate-track="reload">



    <style type="text/css">/* Chart.js */
@-webkit-keyframes chartjs-render-animation{from{opacity:0.99}to{opacity:1}}@keyframes chartjs-render-animation{from{opacity:0.99}to{opacity:1}}.chartjs-render-monitor{-webkit-animation:chartjs-render-animation 0.001s;animation:chartjs-render-animation 0.001s;}</style></head>

<body class="fixed-sn dorar-skin scrollbar-dusty-grass thin body" aria-busy="true">
    <!-- Google Tag Manager (noscript) -->
<noscript><iframe src="https://www.googletagmanager.com/ns.html?id=GTM-TQTFGJFM"
    height="0" width="0" style="display:none;visibility:hidden"></iframe></noscript>
    <!-- End Google Tag Manager (noscript) -->
    

    <!-- div id="mdb-preloader" class="flex-center">
         <div id="preloader-markup"></div>
      </div -->
    <!-- Sidebar navigation -->
    <div id="slide-out" class="side-nav side right-aligned" style="width: 240px; transform: translateX(100%);">
        <ul class="custom-scrollbar ps ps__rtl ps--active-y">
            <!-- Logo -->
            <li class="logo-wrapper waves-light waves-effect waves-light">
                <a class="w-100" href="https://dorar.net/"><img loading="lazy" src="./أحاديث منتشرة لا تصح_files/dorar_logo.svg" alt="dorar_logo" class="img-fluid flex-center px-3 mx-auto"></a>
            </li>
            <!--/. Logo -->
            <!-- a href="#" data-activates="slide-out" class="my-3 btn-close text-center text-muted" id="closeNav" style="line-height: 1rem; position:absolute; right: 15px; top:15px" aria-label="slidout"><i class="fa fa-lg fa-times"></i></a -->

            <!--Social-->

            <li>
                <ul class="social px-0 d-flex justify-content-center">
                    <li>
                        <a target="_blank" href="https://www.facebook.com/pages/mwq-ldrr-lsny-Dorarnet/251615861901?ref=nf" aria-label="تابعونا على فيسبووك" class="icons-sm px-1"><i class="fa fa-facebook">
                            </i></a>
                    </li>
                    <li>
                        <a target="_blank" href="https://x.com/dorarnet" aria-label="تابعونا على منصة إكس" class="icons-sm px-1"><i class="fa icon-x-twitter"> </i></a>
                    </li>
                    <li>
                        <a target="_blank" href="https://telegram.me/dorarnet_telegram" aria-label="تابعونا على تلجرام" class="icons-sm px-1"><i class="fa fa-telegram"> </i></a>
                    </li>
                    <li>
                        <a target="_blank" href="https://www.instagram.com/dorarrnet/" aria-label="تابعونا على إنستاجرام" class="icons-sm px-1"><i class="fa fa-instagram">
                            </i></a>
                    </li>
                    <li>
                        <a target="_blank" href="https://www.youtube.com/user/dorartv" aria-label="تابعونا على يوتيوب" class="icons-sm px-1"><i class="fa fa-youtube"> </i></a>
                    </li>
                    <li>
                        <a target="_blank" href="https://whatsapp.com/channel/0029VaCfdUm8PgsLBK7VWm3G" class="icons-sm px-1" aria-label="تابعونا على واتساب"><i class="fa fa-whatsapp"> </i></a>
                    </li>
                    
                                    </ul>
            </li>
            <!--/Social-->
            <!-- Side navigation links -->
            <li>
                <ul class="collapsible collapsible-accordion pb-4">
                    <li>
                        <a href="https://dorar.net/about" class="collapsible-header waves-effect d-flex justify-content-between align-items-center px-2">
                            التعريف بالموقع <i class="fa fa-caret-left"></i></a>
                    </li>
                    <li>
                        <a href="https://dorar.net/site/gbook" class="collapsible-header waves-effect d-flex justify-content-between align-items-center px-2">
                            علماء أشادوا بالموقع <i class="fa fa-caret-left"></i></a>
                    </li>

                    <li>
                        <a href="https://dorar.net/fake-hadith?page=3#" data-toggle="modal" data-target="#modal1" class="collapsible-header waves-effect d-flex justify-content-between align-items-center px-2">
                            لجنة الإشراف العلمي<i class="fa fa-caret-left"></i></a>
                    </li>
                    <li>
                        <a href="https://dorar.net/fake-hadith?page=3#" data-toggle="modal" data-target="#modal2" class="collapsible-header waves-effect d-flex justify-content-between align-items-center px-2">
                            منهجية عمل الموسوعات <i class="fa fa-caret-left"></i></a>
                    </li>
                    <li>
                        <a href="https://dorar.net/mushrif" class="collapsible-header waves-effect d-flex justify-content-between align-items-center px-2">
                            مداد المشرف <i class="fa fa-caret-left"></i></a>
                    </li>
                    <li>
                        <a href="https://dorar.net/en" class="collapsible-header waves-effect d-flex justify-content-between align-items-center px-2">
                            English <i class="fa fa-caret-left"></i></a>
                    </li>
                    <li>
                        <a href="https://store.dorar.net/" class="collapsible-header waves-effect d-flex justify-content-between align-items-center px-2">
                            المتجر <i class="fa fa-caret-left"></i></a>
                    </li>
                    <li>
                        <a href="https://dorar.net/apps" class="collapsible-header waves-effect d-flex justify-content-between align-items-center px-2">
                            تطبيقات الجوال <i class="fa fa-caret-left"></i></a>
                    </li>
                    <li>

                    </li>
                    <li>
                        <hr>
                    </li>
                    <li>
                        <a href="https://dorar.net/tafseer" class="collapsible-header waves-effect d-flex justify-content-between align-items-center px-2">
                            موسوعة التفسير
                            <i class="fa fa-caret-left"></i>
                        </a>
                    </li>
                    <li>
                        <a href="https://dorar.net/hadith" class="collapsible-header waves-effect d-flex justify-content-between align-items-center px-2">
                            الموسوعة الحديثية <i class="fa fa-caret-left"></i></a>
                    </li>
                    <li>
                        <a href="https://dorar.net/aqeeda" class="collapsible-header waves-effect d-flex justify-content-between align-items-center px-2">
                            الموسوعة العقدية <i class="fa fa-caret-left"></i></a>
                    </li>
                    <li>
                        <a href="https://dorar.net/adyan" class="collapsible-header waves-effect d-flex justify-content-between align-items-center px-2">
                            موسوعة الأديان <i class="fa fa-caret-left"></i></a>
                    </li>
                    <li>
                        <a href="https://dorar.net/frq" class="collapsible-header waves-effect d-flex justify-content-between align-items-center px-2">
                            موسوعة الفرق <i class="fa fa-caret-left"></i></a>
                    </li>
                    <li>
                        <a href="https://dorar.net/feqhia" class="collapsible-header waves-effect d-flex justify-content-between align-items-center px-2">
                            الموسوعة الفقهية <i class="fa fa-caret-left"></i></a>
                    </li>
                    <li>
                        <a href="https://dorar.net/osolfeqh" class="collapsible-header waves-effect d-flex justify-content-between align-items-center px-2">
                            موسوعة أصول الفقه <i class="fa fa-caret-left"></i></a>
                    </li>

                    <li>
                        <a href="https://dorar.net/qfiqhia" class="collapsible-header waves-effect d-flex justify-content-between align-items-center px-2">
                            موسوعة القواعد الفقهية <i class="fa fa-caret-left"></i></a>
                    </li>

                    <li>
                        <a href="https://dorar.net/alakhlaq" class="collapsible-header waves-effect d-flex justify-content-between align-items-center px-2">
                            موسوعة الأخلاق والسلوك<i class="fa fa-caret-left"></i></a>
                    </li>
                    <li>
                        <a href="https://dorar.net/aadab" class="collapsible-header waves-effect d-flex justify-content-between align-items-center px-2">
                            موسوعة الآداب الشرعية <i class="fa fa-caret-left"></i></a>
                    </li>
                    <li>
                        <a href="https://dorar.net/history" class="collapsible-header waves-effect d-flex justify-content-between align-items-center px-2">
                            الموسوعة التاريخية <i class="fa fa-caret-left"></i></a>
                    </li>

                    <li>
                        <a href="https://dorar.net/arabia" class="collapsible-header waves-effect d-flex justify-content-between align-items-center px-2">
                            موسوعة اللغة العربية <i class="fa fa-caret-left"></i></a>
                    </li>
                    <li>
                        <a href="https://dorar.net/fake-hadith" class="collapsible-header waves-effect d-flex justify-content-between align-items-center px-2">
                            أحاديث منتشرة لا تصح <i class="fa fa-caret-left"></i></a>
                    </li>
                    <li>
                        <hr>
                    </li>
                    <li>
                        <a href="https://dorar.net/article/category/3" class="collapsible-header waves-effect d-flex justify-content-between align-items-center px-2">
                            مقالات وبحوث <i class="fa fa-caret-left"></i></a>
                    </li>
                    <li>
                        <a href="https://dorar.net/selection" class="collapsible-header waves-effect d-flex justify-content-between align-items-center px-2">
                            نفائس الموسوعات <i class="fa fa-caret-left"></i></a>
                    </li>
                    <li>
                        <a href="https://dorar.net/article/books" class="collapsible-header waves-effect d-flex justify-content-between align-items-center px-2">
                            قراءة في كتاب <i class="fa fa-caret-left"></i></a>
                    </li>
                    <li>
                        <a href="https://dorar.net/contribute" class="collapsible-header waves-effect d-flex justify-content-between align-items-center px-2">
                            شارك معنا <i class="fa fa-caret-left"></i></a>
                    </li>
                    <li>
                        <hr>
                    </li>
                                    </ul>
            </li>
            <!--/. Side navigation links -->
        <div class="ps__rail-x" style="left: 0px; bottom: 0px;"><div class="ps__thumb-x" tabindex="0" style="left: 0px; width: 0px;"></div></div><div class="ps__rail-y" style="top: 0px; height: 788px; right: 225px;"><div class="ps__thumb-y" tabindex="0" style="top: 0px; height: 384px;"></div></div></ul>
        <div class="sidenav-bg" style="width: 240px;"></div>
    </div>

            <!-- Start your project here -->
<header class="h-auto w-100">

    <!-- Main Navigation -->
    <div class="z-depth-0 default-gradient pt-1 pt-md-1 pt-sm-1 pt-xl-0 pt-lg-0">
        <div class="container-fluid">
            <div class="row justify-content-center">
                <div class="col-12 col-lg-11 col-xl-11 d-none d-lg-block d-xl-block d-xl-none">
                    <!--Navbar -->
                    <nav class="navbar d-flex justify-content-between align-items-center w-100 px-0 navbar-expand-lg navbar-dark z-depth-0 bg-transparent">
                        <ul class="navbar-nav readMore">
                            <li class="nav-item">
                                <a target="_blank" class="nav-link d-flex align-items-center waves-effect waves-light" href="https://store.dorar.net/"><i class="fa fa-cart-arrow-down px-2"></i> المتجر
                                </a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link d-flex align-items-center waves-effect waves-light" href="https://dorar.net/fake-hadith?page=3#" data-toggle="modal" data-target="#modal1"><i class="fa fa-users px-2"></i>لجنة الإشراف العلمي</a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link d-flex align-items-center waves-effect waves-light" href="https://dorar.net/site/gbook"><i class="fa fa-pencil-square-o px-2"></i> علماء أشادوا بالموقع </a>
                            </li>
                            
                            
                            
                            
                            

                        <li class="flexMenu-viewMore"><a href="https://dorar.net/fake-hadith?page=3#" title="المزيد" class=" waves-effect waves-light">•••</a><ul class="flexMenu-popup" style="display:none; position: absolute; top: 44px;"><li class="nav-item">
                                <a class="nav-link d-flex align-items-center waves-effect waves-light" href="https://dorar.net/fake-hadith?page=3#" data-toggle="modal" data-target="#modal2"><i class="fa fa-info-circle px-2"></i>منهج العمل في
                                    الموسوعات</a>
                            </li><li class="nav-item">
                                <a class="nav-link d-flex align-items-center waves-effect waves-light" href="https://dorar.net/about">
                                    <i class="fa fa-handshake-o px-2"></i> التعريف
                                    بالموقع</a>
                            </li><li class="nav-item">
                                <a class="nav-link d-flex align-items-center waves-effect waves-light" href="https://dorar.net/mushrif">
                                    <i class="fa fa-pencil-square-o px-2"></i> مداد
                                    المشرف</a>
                            </li><li class="nav-item">
                                <a class="nav-link d-flex align-items-center waves-effect waves-light" href="https://dorar.net/apps">
                                    <i class="fa fa-mobile fa-lg px-2"></i> تطبيقات
                                    الجوال</a>
                            </li></ul></li></ul>
                        <ul class="navbar-nav nav-flex-icons">
                            <li class="nav-item">
                                <a class="switch_btn btn-floating btn-sm btn-outline-default switch toggle_btn pushme waves-effect waves-light" role="button" href="https://dorar.net/fake-hadith?page=3#" data-switch-dark="" aria-label="dark mode switch">
                                    <i class="fa icon-night state-open"></i>
                                    <i class="fa fa-2x icon-day state-close d-none"></i>
                                </a>
                            </li>
                            <li class="nav-item">
                                <a target="_blank" class="btn-floating btn-sm btn-outline-white waves-effect waves-light" href="https://www.facebook.com/pages/mwq-ldrr-lsny-Dorarnet/251615861901?ref=nf" aria-label="تابعونا على فيسبووك"><i class="fa fa-facebook"></i></a>
                            </li>
                            <li class="nav-item">
                                <a target="_blank" href="https://twitter.com/dorarnet" aria-label="تابعونا على تويتر" class="btn-floating btn-sm btn-outline-white waves-effect waves-light"><i class="fa icon-x-twitter"></i></a>
                            </li>
                            <li target="_blank" class="nav-item">
                                <a href="https://telegram.me/dorarnet_telegram" class="btn-floating btn-sm btn-outline-white waves-effect waves-light" aria-label="تابعونا على تلجرام"><i class="fa fa-telegram"></i></a>
                            </li>
                            <li class="nav-item">
                                <a target="_blank" href="https://www.instagram.com/dorarrnet/" aria-label="تابعونا على إنستاجرام" class="btn-floating btn-sm btn-outline-white waves-effect waves-light"><i class="fa fa-instagram"></i></a>
                            </li>
                            <li class="nav-item">
                                <a target="_blank" href="https://www.youtube.com/user/dorartv" aria-label="تابعونا على يوتيوب" class="btn-floating btn-sm btn-outline-white waves-effect waves-light"><i class="fa fa-youtube"></i></a>
                            </li>
                            <li class="nav-item">
                                <a target="_blank" href="https://www.whatsapp.com/channel/0029VaCfdUm8PgsLBK7VWm3G" class="btn-floating btn-sm btn-outline-white waves-effect waves-light" aria-label="راسلنا على واتساب"><i class="fa fa-whatsapp"></i></a>
                            </li>
                                                            <li class="nav-item d-flex align-items-center">
                                    <a href="https://dorar.net/fake-hadith?page=3#" data-toggle="modal" data-target="#modalLRForm" class="btn-sm btn-outline-white px-2 mx-2 d-flex align-items-center waves-effect waves-light"><i class="fa fa-user px-2"></i> تسجيل الدخول</a>
                                </li>
                            
                            
                                                    </ul>
                    </nav>
                    <!--/.Navbar -->
                </div>
            </div>
        </div>
    </div>
    <!-- Navbar -->
    <div class="z-depth-0 header_custom_bg">
        <!-- example 1 - using absolute position for center -->
        <div class="container-fluid">
            <div class="row justify-content-center">
                <div class="col-12 col-lg-11 col-xl-11 pb-2 pb-md-2 pb-lg-3 pt-2 pt-md-2 pt-lg-3">
                    <div class="row">
                        <div class="col-12 d-flex justify-content-between align-items-center d-lg-none d-xl-none">
                            <div class="d-flex">
                                <a href="https://dorar.net/fake-hadith?page=3#" data-activates="slide-out" class="button-collapse2" aria-label="slidout">
                                    <div class="animated-icon2">
                                        <span></span><span></span><span></span><span></span>
                                    </div>
                                </a>
                            </div>
                            <a class="d-flex abs mx-0 px-3 px-sm-3 px-md-3 px-lg-5 px-xl-5" href="https://dorar.net/">
                                <img alt="logo" src="./أحاديث منتشرة لا تصح_files/_logo.svg" class="img-fluid mx-auto"></a>
                            <div class="d-flex">
                                <a target="_blank" href="https://store.dorar.net/" class="nav-link px-0 d-flex align-items-center" aria-label="Store">
                                    <i class="fa fa-shopping-cart fa-2x px-1 default-text-color"></i>
                                </a>
                                                                    <a class="nav-link px-0 d-flex align-items-center" data-toggle="modal" data-target="#modalLRForm">
                                        <!-- <img src="img/lang.svg" width="35" alt="lang" class="px-1" /> -->
                                        <i class="fa fa-user-circle-o fa-2x px-1 default-text-color"></i>
                                    </a>
                                
                            </div>
                        </div>
                        <div class="col-lg-6 col-12 d-none d-lg-block d-xl-block d-xl-none">
                            <div class="d-flex justify-content-start align-items-center">
                                <!-- SideNav slide-out button -->
                                <a href="https://dorar.net/fake-hadith?page=3#" data-activates="slide-out" class="d-flex button-collapse2" aria-label="slidout">
                                    <div class="animated-icon2">
                                        <span></span><span></span><span></span><span></span>
                                    </div>
                                </a>
                                <a class="d-flex mx-4 abs" href="https://dorar.net/">
                                    <img alt="logo" src="./أحاديث منتشرة لا تصح_files/_logo.svg" class="img-fluid" width="400"></a>
                            </div>
                        </div>
                        <div class="col-lg-6 col-12 d-none d-lg-flex d-xl-flex d-xl-none justify-content-end align-items-center pt-2 pt-md-2 pt-lg-0 pt-xl-0 px-0 px-md-0 px-lg-3 px-xl-3">
                            <!-- Search form -->
                            <div class="position-relative d-flex justify-content-end align-items-center h-100 w-100 mx-3">
                                <div class="collapse position-absolute w-100" id="searchForm2">
                                    <div class="d-flex input-group form-sm form-2 z-depth-3 bg-white rounded rounded-md-0 mx-0">
                                        <!-- Basic dropdown -->
                                        <button class="btn-search btn btn-link dropdown-toggle m-0 px-2 px-lg-3 border-left border-right d-flex align-items-center waves-effect waves-light" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">اختر نوع البحث</button>
                                        <div class="dropdown-menu">
                                            <a tag="/site/search" class="srch-scope dropdown-item" href="https://dorar.net/fake-hadith?page=3#">البحث الشامل</a>
                                            <a tag="/tafseer/search" class="srch-scope dropdown-item" href="https://dorar.net/fake-hadith?page=3#">موسوعة التفسير</a>
                                            <a tag="/hadith/search" class="srch-scope dropdown-item" href="https://dorar.net/fake-hadith?page=3#">الموسوعة الحديثية</a>
                                            <a tag="/aqeeda/search" class="srch-scope dropdown-item" href="https://dorar.net/fake-hadith?page=3#">الموسوعة العقدية</a>
                                            <a tag="/adyan/search" class="srch-scope dropdown-item" href="https://dorar.net/fake-hadith?page=3#">موسوعة الأديان</a>
                                            <a tag="/frq/search" class="srch-scope dropdown-item" href="https://dorar.net/fake-hadith?page=3#">موسوعة الفرق</a>

                                            <a tag="/feqhia/search" class="srch-scope dropdown-item" href="https://dorar.net/fake-hadith?page=3#">الموسوعة الفقهية</a>
                                                <a tag="/osolfeqh" class="srch-scope dropdown-item" href="https://dorar.net/fake-hadith?page=3#">موسوعة أصول الفقه</a>
                                                <a tag="/qfiqhia" class="srch-scope dropdown-item" href="https://dorar.net/fake-hadith?page=3#"> موسوعة القواعد الفقهية
                                            </a><a tag="/alakhlaq/search" class="srch-scope dropdown-item" href="https://dorar.net/fake-hadith?page=3#">موسوعة الأخلاق</a>
                                                <a tag="/aadab/search" class="srch-scope dropdown-item" href="https://dorar.net/fake-hadith?page=3#">موسوعة الآداب الشرعية</a>
                                            <a tag="/history/search" class="srch-scope dropdown-item" href="https://dorar.net/fake-hadith?page=3#">الموسوعة التاريخية</a>
                                            <a tag="/arabia/search" class="srch-scope dropdown-item" href="https://dorar.net/fake-hadith?page=3#">موسوعة اللغة العربية</a>
                                            <a tag="/fake-hadith" class="srch-scope dropdown-item" href="https://dorar.net/fake-hadith?page=3#">الأحاديث المنتشرة</a>
                                        </div>
                                        <!-- Basic dropdown -->
                                        <input id="qq" class="form-control form-control-lg my-0 py-1 rounded rounded-md-0 border-0 bg-transparent fa-sm" type="text" placeholder="البحث .." aria-label="Search">
                                        <div class="input-group-append mx-0">
                                            <button type="button" id="do-search" tag="#qq" class="do-search ssrch input-group-text custom_input_group_tbutton_1 bg-transparent border-0" aria-label="Do Search">
                                                <i class="fa fa-search default-text-light" aria-hidden="true"></i>
                                            </button>
                                            <button type="button" id="button-play-ws" class="input-group-text custom_input_group_tbutton_2 bg-transparent border-0" aria-label="Sound search">
                                                <i class="fa fa-microphone default-text-light" aria-hidden="true"></i>
                                            </button>
                                        </div>
                                        <div class="input-group-append">
                                            <a class="btn bg-light m-0 py-2 px-3 z-depth-0 waves-effect rounded d-flex align-items-center" href="https://dorar.net/fake-hadith?page=3#searchForm2" data-target="#searchForm2" data-toggle="collapse">
                                                <i class="fa fa-close fa-lg"></i>
                                            </a>
                                        </div>
                                    </div>
                                </div>
                                <a class="nav-item nav-link px-0 default-text-color" title="البحث الشامل" href="https://dorar.net/fake-hadith?page=3#searchForm2" data-target="#searchForm2" data-toggle="collapse">
                                    <i class="fa-lg fa fa-search"></i>
                                </a>
                            </div>
                            <div class="d-none d-lg-block d-xl-block d-xl-none">
                                <div class="d-flex justify-content-end">
                                    <span class="nav-link dropdown-toggle px-0 d-flex align-items-center" id="navbarDropdownMenuLink-333" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                                        <img src="./أحاديث منتشرة لا تصح_files/lang.svg" width="45" alt="lang" class="px-2">
                                        اللغة
                                    </span>
                                    <div class="dropdown-menu dropdown-menu-left dropdown-default" aria-labelledby="navbarDropdownMenuLink-333">
                                        <a class="dropdown-item" href="https://dorar.net/en">En</a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!-- /.Navbar -->
    </div>
    <div class="fixed-bottom" style="bottom: 0px;">
        <div class="collapse position-absolute w-100 bottom-0" id="searchForm">
            <!-- Search form -->
            <div class="d-flex justify-content-between align-items-center input-group form-sm form-2 z-depth-2 bg-white rounded rounded-md-0 mx-0">
                <!-- Basic dropdown -->
                <button class="btn-search btn btn-link dropdown-toggle m-0 px-2 px-lg-3 border-left border-right d-flex align-items-center waves-effect waves-light" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">اختر نوع
                    البحث</button>
                <div class="dropdown-menu">
                    <a tag="/tafseer/search" class="srch-scope dropdown-item" href="https://dorar.net/fake-hadith?page=3#">موسوعة التفسير</a>
                    <a tag="/hadith/search" class="srch-scope dropdown-item" href="https://dorar.net/fake-hadith?page=3#">الموسوعة الحديثية</a>
                    <a tag="/aqeeda/search" class="srch-scope dropdown-item" href="https://dorar.net/fake-hadith?page=3#">الموسوعة العقدية</a>
                    <a tag="/adyan/search" class="srch-scope dropdown-item" href="https://dorar.net/fake-hadith?page=3#">موسوعة الأديان</a>
                    <a tag="/frq/search" class="srch-scope dropdown-item" href="https://dorar.net/fake-hadith?page=3#">موسوعة الفرق</a>
                    <a tag="/feqhia/search" class="srch-scope dropdown-item" href="https://dorar.net/fake-hadith?page=3#">الموسوعة الفقهية</a>
                    <a tag="/osolfeqh" class="srch-scope dropdown-item" href="https://dorar.net/fake-hadith?page=3#">موسوعة أصول الفقه</a>
                    <a tag="/alakhlaq/search" class="srch-scope dropdown-item" href="https://dorar.net/fake-hadith?page=3#">موسوعة الأخلاق</a>
                    <a tag="/aadab/search" class="srch-scope dropdown-item" href="https://dorar.net/fake-hadith?page=3#">موسوعة اللآداب الشرعية</a>
                    <a tag="/history/search" class="srch-scope dropdown-item" href="https://dorar.net/fake-hadith?page=3#">الموسوعة التاريخية</a>
                    <a tag="/arabia/search" class="srch-scope dropdown-item" href="https://dorar.net/fake-hadith?page=3#">موسوعة اللغة العربية</a>
                    <a tag="/fake-hadith" class="srch-scope dropdown-item" href="https://dorar.net/fake-hadith?page=3#">الأحاديث المنتشرة</a>
                </div>
                <!-- Basic dropdown -->
                <input id="qq2" class="form-control form-control-lg my-0 py-1 rounded rounded-md-0 border-0 bg-transparent fa-sm" type="text" placeholder="البحث .." aria-label="Search">
                <div class="input-group-append mx-0">
                    <button type="button" id="do-search-mobile" tag="#qq2" class="do-search input-group-text custom_input_group_tbutton_1 bg-transparent border-0">
                        <i class="fa fa-search default-text-light" aria-hidden="true"></i>
                    </button>
                    <button class="input-group-text custom_input_group_tbutton_2 bg-transparent border-0">
                        <i class="fa fa-microphone default-text-light" aria-hidden="true"></i>
                    </button>
                </div>
                <div class="input-group-append">
                    <a class="btn p-3 bg-light m-0 d-flex align-items-center waves-effect waves-light" href="https://dorar.net/fake-hadith?page=3#searchForm" data-target="#searchForm" data-toggle="collapse">
                        <i class="fa fa-close fa-lg"></i>
                    </a>
                </div>
            </div>
        </div>
        <div class="z-depth-0 header_custom_bg pt-1 d-flex d-lg-none d-xl-none w-100">
            <div class="container-fluid px-0 px-md-0 px-lg-3 px-xl-3">
                <div class="row justify-content-center">
                    <div class="col-lg-6 col-12 d-flex justify-content-between align-items-center pt-0">
                        <nav class=" nav nav-pills nav-justified w-100">
                            
                            
                                                            <div class="nav-item dropdown">
                                    <a href="https://dorar.net/fake-hadith?page=3#" class="nav-item nav-link default-text-color" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">

                                        <img src="./أحاديث منتشرة لا تصح_files/font-size.svg" alt="font-size" width="18">
                                    </a>
                                    <div class="dropdown-menu custom_fontsize_dropdown">
                                        <a tag="1.06" step="0.01" class="dropdown-item default-text-color font-btn" href="https://dorar.net/fake-hadith?page=3#"> + <img src="./أحاديث منتشرة لا تصح_files/font-size.svg" alt="font-size" width="18"></a>
                                        <a tag="1.05" step="0.0" class="dropdown-item default-text-color font-btn" href="https://dorar.net/fake-hadith?page=3#"> = <img src="./أحاديث منتشرة لا تصح_files/font-size.svg" alt="font-size" width="18"></a>
                                        <a tag="1.04" step="-0.01" class="dropdown-item default-text-color font-btn" href="https://dorar.net/fake-hadith?page=3#"> - <img src="./أحاديث منتشرة لا تصح_files/font-size.svg" alt="font-size" width="18"></a>
                                    </div>
                                </div>
                            
                            
                                                            <a class="switch_btn nav-item nav-link default-text-color switch pushme" role="button" href="https://dorar.net/fake-hadith?page=3#" data-switch2-dark="" aria-label="dark mode switch2">
                                    <i class="fa fa-lg icon-night state-open"></i>
                                    <i class="fa fa-2x icon-day state-close d-none"></i>
                                </a>
                            
                            
                            <a href="https://dorar.net/fake-hadith?page=3#" data-toggle="modal" data-target="#modalSocial" title="مشاركة" class="nav-item nav-link default-text-color">
                                <i class="fa-lg fa fa-share-alt"></i>
                            </a>

                                                            <a href="https://dorar.net/feedback/error-report?link=https://dorar.net/fake-hadith" data-toggle="tooltip" title="" class="nav-item nav-link default-text-color" data-original-title="نبه عن خطأ">
                                    <i class="fa-lg fa fa-warning"></i>
                                </a>
                                                        <a class="nav-item nav-link default-text-color" title="البحث الشامل" href="https://dorar.net/fake-hadith?page=3#searchForm" data-target="#searchForm" data-toggle="collapse">
                                <i class="fa-lg fa fa-search"></i>
                            </a>
                        </nav>
                    </div>
                </div>
            </div>
        </div>
    </div>
</header>
    
    <!-- Large modal -->
<div class="modal fade" id="modal2" tabindex="-1" role="dialog" aria-labelledby="myLargeModalLabel" aria-hidden="true">
<div class="modal-dialog modal-lg">
   <div class="modal-content">
      <div class="modal-header">
         <h5 class="modal-title w-100" id="myModalLabel">منهج العمل في الموسوعات</h5>
         <button type="button" class="close" data-dismiss="modal" aria-label="Close">
         <span aria-hidden="true">×</span>
         </button>
      </div>
      <div class="modal-body">
          <!--Accordion wrapper-->
          <div class="accordion md-accordion dorar_custom_accordion amiri_custom_content" id="accordionEx" role="tablist" aria-multiselectable="true">
          <!-- Accordion card -->
          <div class="card z-depth-0 mb-0">
             <!-- Card header -->
             <div class="card-header p-0" role="tab" id="headingOne1">
                <a class="collapsed" data-toggle="collapse" data-parent="#accordionEx" href="https://dorar.net/fake-hadith?page=3#collapseOne1" aria-expanded="false" aria-controls="collapseOne1">
                   <h6 class="h6-responsive mb-0 d-flex align-items-center justify-content-between b-0 py-3 px-1">
                     <div><i class="fa fa-file-o px-2" aria-hidden="true"></i> موسوعة التفسير </div>
                     <i class="fa fa-angle-down rotate-icon px-3"></i>
                   </h6>
                </a>
             </div>
             <!-- Card body -->
             <div id="collapseOne1" class="collapse" role="tabpanel" aria-labelledby="headingOne1" data-parent="#accordionEx">
                <div class="card-body px-0 py-3 pt-0 text-center">
                   <p>
                           <a class="title-manhag" href="https://dorar.net/article/1955">منهج العمل في الموسوعة
                           </a>
                       </p>
                       <p class="subtitle-manhag">راجع الموسوعة</p>
                       <p>الشيخ الدكتور خالد بن عثمان السبت</p>
                       <p class="ustaz">أستاذ التفسير بجامعة الإمام عبدالرحمن بن فيصل</p>
                       <p>الشيخ الدكتور أحمد سعد الخطيب</p>
                       <p class="ustaz">أستاذ التفسير بجامعة الأزهر</p>

                       <p class="subtitle-manhag">اعتمد المنهجية</p>
                       <p class="subtitle-manhag">بالإضافة إلى المراجعَين</p>
                       <p>الشيخ الدكتور عبدالرحمن بن معاضة الشهري</p>
                       <p class="ustaz">أستاذ التفسير بجامعة الملك سعود</p>
                       <p>الشيخ الدكتور مساعد بن سليمان الطيار</p>
                       <p class="ustaz">أستاذ التفسير بجامعة الملك سعود</p>
                       <p>الشيخ الدكتور منصور بن حمد العيدي</p>
                       <p class="ustaz">
                           أستاذ التفسير بجامعة الإمام عبدالرحمن بن فيصل</p>
                </div>
             </div>
          </div>
          <!-- Accordion card -->
          <!-- Accordion card -->
          <div class="card z-depth-0 mb-0">
             <!-- Card header -->
             <div class="card-header p-0" role="tab" id="tabb-1">
                <a class="collapsed" data-toggle="collapse" data-parent="#accordionEx" href="https://dorar.net/fake-hadith?page=3#collapseTwo2" aria-expanded="false" aria-controls="collapseTwo2">
                   <h6 class="h6-responsive mb-0 d-flex align-items-center justify-content-between b-0 py-3 px-1">
                      <div><i class="fa fa-file-o px-2" aria-hidden="true"></i> الموسوعة الحديثية</div>
                      <i class="fa fa-angle-down rotate-icon px-3"></i>
                   </h6>
                </a>
             </div>
             <!-- Card body -->
             <div id="collapseTwo2" class="collapse" role="tabpanel" aria-labelledby="tabb-1" data-parent="#accordionEx">
                <div class="card-body px-0 py-3 pt-0 text-center">
                   <p>
                       <a class="title-manhag" href="https://dorar.net/article/77">منهج العمل في الموسوعة</a>
                   </p>
                </div>
             </div>
          </div>
          <!-- Accordion card -->
          <!-- Accordion card -->
          <div class="card z-depth-0 mb-0">
             <!-- Card header -->
             <div class="card-header p-0" role="tab" id="tabb-2">
                <a class="collapsed" data-toggle="collapse" data-parent="#accordionEx" href="https://dorar.net/fake-hadith?page=3#collapseTwo3" aria-expanded="false" aria-controls="collapseTwo3">
                   <h6 class="h6-responsive mb-0 d-flex align-items-center justify-content-between b-0 py-3 px-1">
                      <div><i class="fa fa-file-o px-2" aria-hidden="true"></i> الموسوعة العقدية</div>
                      <i class="fa fa-angle-down rotate-icon px-3"></i>
                   </h6>
                </a>
             </div>
             <!-- Card body -->
             <div id="collapseTwo3" class="collapse" role="tabpanel" aria-labelledby="tabb-2" data-parent="#accordionEx">
                <div class="card-body px-0 py-3 pt-0 text-center">
                   <p>
                       <a class="title-manhag" href="https://dorar.net/article/1987">منهج العمل في الموسوعة</a>
                   </p>
                </div>
             </div>
          </div>
          <!-- Accordion card -->

          <!-- Accordion card -->
          <div class="card z-depth-0 mb-0">
             <!-- Card header -->
             <div class="card-header p-0" role="tab" id="tabb-3">
                <a class="collapsed" data-toggle="collapse" data-parent="#accordionEx" href="https://dorar.net/fake-hadith?page=3#collapseTwo4" aria-expanded="false" aria-controls="collapseTwo4">
                   <h6 class="h6-responsive mb-0 d-flex align-items-center justify-content-between b-0 py-3 px-1">
                      <div><i class="fa fa-file-o px-2" aria-hidden="true"></i> موسوعة الأديان</div>
                      <i class="fa fa-angle-down rotate-icon px-3"></i>
                   </h6>
                </a>
             </div>
             <!-- Card body -->
             <div id="collapseTwo4" class="collapse" role="tabpanel" aria-labelledby="tabb-3" data-parent="#accordionEx">
                <div class="card-body px-0 py-3 pt-0 text-center">
                   <p>
                       <a class="title-manhag" href="https://dorar.net/article/1989">منهج العمل في الموسوعة</a>
                   </p>
                </div>
             </div>
          </div>
          <!-- Accordion card -->

          <!-- Accordion card -->
          <div class="card z-depth-0 mb-0">
             <!-- Card header -->
             <div class="card-header p-0" role="tab" id="tabb-4">
                <a class="collapsed" data-toggle="collapse" data-parent="#accordionEx" href="https://dorar.net/fake-hadith?page=3#collapseTwo5" aria-expanded="false" aria-controls="collapseTwo5">
                   <h6 class="h6-responsive mb-0 d-flex align-items-center justify-content-between b-0 py-3 px-1">
                      <div><i class="fa fa-file-o px-2" aria-hidden="true"></i> موسوعة الفرق</div>
                      <i class="fa fa-angle-down rotate-icon px-3"></i>
                   </h6>
                </a>
             </div>
             <!-- Card body -->
             <div id="collapseTwo5" class="collapse" role="tabpanel" aria-labelledby="tabb-4" data-parent="#accordionEx">
                <div class="card-body px-0 py-3 pt-0 text-center">
                   <p>
                       <a class="title-manhag" href="https://dorar.net/article/1990">منهج العمل في الموسوعة</a>
                   </p>
                </div>
             </div>
          </div>
          <!-- Accordion card -->

          <!-- Accordion card -->
          <div class="card z-depth-0 mb-0">
             <!-- Card header -->
             <div class="card-header p-0" role="tab" id="tabb-5">
                <a class="collapsed" data-toggle="collapse" data-parent="#accordionEx" href="https://dorar.net/fake-hadith?page=3#collapseTwo7" aria-expanded="false" aria-controls="collapseTwo7">
                   <h6 class="h6-responsive mb-0 d-flex align-items-center justify-content-between b-0 py-3 px-1">
                      <div><i class="fa fa-file-o px-2" aria-hidden="true"></i> الموسوعة الفقهية</div>
                      <i class="fa fa-angle-down rotate-icon px-3"></i>
                   </h6>
                </a>
             </div>
             <!-- Card body -->
             <div id="collapseTwo7" class="collapse" role="tabpanel" aria-labelledby="tabb-5" data-parent="#accordionEx">
                <div class="card-body px-0 py-3 pt-0 text-center">
                   <p>
                           <a class="title-manhag" href="https://dorar.net/article/1923">منهج العمل في الموسوعة
                           </a>
                       </p>
                       <p class="ustaz">
                           تم اعتماد المنهجية من<br>
                           <span>الجمعية الفقهية السعودية
                               <br></span>

                           برئاسة الشيخ الدكتور<br>
                           <span>سعد بن تركي الخثلان<br></span>
                           أستاذ الفقه بجامعة الإمام محمد بن سعود
                           <br>
                           عضو هيئة كبار العلماء (سابقاً)</p>
                </div>
             </div>
          </div>
          <!-- Accordion card -->
          <div class="card z-depth-0 mb-0">
            <!-- Card header -->
            <div class="card-header p-0" role="tab" id="hosolfeqh">
               <a class="collapsed" data-toggle="collapse" data-parent="#accordionEx" href="https://dorar.net/fake-hadith?page=3#colosolfeqh" aria-expanded="false" aria-controls="colosolfeqh">
                  <h6 class="h6-responsive mb-0 d-flex align-items-center justify-content-between b-0 py-3 px-1">
                    <div><i class="fa fa-file-o px-2" aria-hidden="true"></i> موسوعة أصول الفقه </div>
                    <i class="fa fa-angle-down rotate-icon px-3"></i>
                  </h6>
               </a>
            </div>
            <!-- Card body -->
            <div id="colosolfeqh" class="collapse" role="tabpanel" aria-labelledby="hosolfeqh" data-parent="#accordionEx">
               <div class="card-body px-0 py-3 pt-0 text-center">
                  <p>
                          <a class="title-manhag" href="https://dorar.net/article/2109">منهج العمل في الموسوعة
                          </a>
                      </p>
                      <p class="subtitle-manhag">راجع الموسوعة</p>
                      <p>الشيخ الدكتور أحمد بن عبدالله بن حميد</p>
                      <p class="ustaz" style="padding-bottom: 10px;">أستاذ أصول الفقه بجامعة أم القرى - سابقا</p>

                      <p>الأستاذ الدكتور عياض بن نامي السلمي</p>
                      <p class="ustaz" style="padding-bottom: 10px;">أستاذ أصول الفقه بجامعة الإمام محمد بن سعود - سابقا</p>

                      <p>الأستاذ الدكتور محمد بن حسين الجيزاني</p>
                      <p class="ustaz">أستاذ أصول الفقه بالجامعة الإسلامية - سابقا</p>


               </div>
            </div>
         </div>
         <!-- Accordion card -->
         <div class="card z-depth-0 mb-0">
            <!-- Card header -->
            <div class="card-header p-0" role="tab" id="hwfiqhia">
               <a class="collapsed" data-toggle="collapse" data-parent="#accordionEx" href="https://dorar.net/fake-hadith?page=3#colowfiqhi" aria-expanded="false" aria-controls="colowfiqhi">
                  <h6 class="h6-responsive mb-0 d-flex align-items-center justify-content-between b-0 py-3 px-1">
                    <div><i class="fa fa-file-o px-2" aria-hidden="true"></i> موسوعة القواعد الفقهية </div>
                    <i class="fa fa-angle-down rotate-icon px-3"></i>
                  </h6>
               </a>
            </div>
            <!-- Card body -->
            <div id="colowfiqhi" class="collapse" role="tabpanel" aria-labelledby="hwfiqhia" data-parent="#accordionEx">
               <div class="card-body px-0 py-3 pt-0 text-center">
                  <p>
                          <a class="title-manhag" href="https://dorar.net/article/2117">منهج العمل في الموسوعة
                          </a>
                      </p>


                <p class="subtitle-manhag">اعتمد المنهج</p>

                <p>الشيخ الدكتور أحمد بن عبدالله بن حميد</p>

                <p class="ustaz" style="padding-bottom: 10px;">أستاذ أصول الفقه بجامعة أم القرى - سابقا</p>

                <p>الأستاذ الدكتور عياض بن نامي السلمي</p>

                <p class="ustaz" style="padding-bottom: 10px;">أستاذ أصول الفقه بجامعة الإمام محمد بن سعود - سابقا</p>

                <p>الأستاذ الدكتور محمد بن حسين الجيزاني</p>

                <p class="ustaz" style="padding-bottom: 10px;">أستاذ أصول الفقه بالجامعة الإسلامية - سابقا</p>

                <p>الأستاذ الدكتور أنور صالح أبوزيد</p>

                <p class="ustaz">أستاذ الفقه وأصوله وعضو هيئة التدريس بكلية المسجد النبوي - سابقا</p>


               </div>
            </div>
         </div>
          <!-- Accordion card -->
          <div class="card z-depth-0 mb-0">
             <!-- Card header -->
             <div class="card-header p-0" role="tab" id="tabb-6">
                <a class="collapsed" data-toggle="collapse" data-parent="#accordionEx" href="https://dorar.net/fake-hadith?page=3#collapseTwo8" aria-expanded="false" aria-controls="collapseTwo8">
                   <h6 class="h6-responsive mb-0 d-flex align-items-center justify-content-between b-0 py-3 px-1">
                      <div><i class="fa fa-file-o px-2" aria-hidden="true"></i> موسوعة الأخلاق والسلوك</div>
                      <i class="fa fa-angle-down rotate-icon px-3"></i>
                   </h6>
                </a>
             </div>
             <!-- Card body -->
             <div id="collapseTwo8" class="collapse" role="tabpanel" aria-labelledby="tabb-6" data-parent="#accordionEx">
                <div class="card-body px-0 py-3 pt-0 text-center">
                   <p>
                       <a class="title-manhag" href="https://dorar.net/article/1988">منهج العمل في الموسوعة</a>
                   </p>
                </div>
             </div>
          </div>
          <!-- Accordion card -->
            <!-- Accordion card -->
            <div class="card z-depth-0 mb-0">
                <!-- Card header -->
                <div class="card-header p-0" role="tab" id="tabb-9">
                <a class="collapsed" data-toggle="collapse" data-parent="#accordionEx" href="https://dorar.net/fake-hadith?page=3#collaadab" aria-expanded="false" aria-controls="collaadab">
                    <h6 class="h6-responsive mb-0 d-flex align-items-center justify-content-between b-0 py-3 px-1">
                        <div><i class="fa fa-file-o px-2" aria-hidden="true"></i> موسوعة الأدب</div>
                        <i class="fa fa-angle-down rotate-icon px-3"></i>
                    </h6>
                </a>
                </div>
                <!-- Card body -->
                <div id="collaadab" class="collapse" role="tabpanel" aria-labelledby="tabb-9" data-parent="#accordionEx">
                <div class="card-body px-0 py-3 pt-0 text-center">
                    <p>
                        <a class="title-manhag" href="https://dorar.net/article/2112">منهج العمل في الموسوعة</a>

                    </p>
                </div>
                </div>
            </div>
          <!-- Accordion card -->
          <div class="card z-depth-0 mb-0">
             <!-- Card header -->
             <div class="card-header p-0" role="tab" id="tabb-7">
                <a class="collapsed" data-toggle="collapse" data-parent="#accordionEx" href="https://dorar.net/fake-hadith?page=3#collapseTwo9" aria-expanded="false" aria-controls="collapseTwo9">
                   <h6 class="h6-responsive mb-0 d-flex align-items-center justify-content-between b-0 py-3 px-1">
                      <div><i class="fa fa-file-o px-2" aria-hidden="true"></i> الموسوعة التاريخية</div>
                      <i class="fa fa-angle-down rotate-icon px-3"></i>
                   </h6>
                </a>
             </div>
             <!-- Card body -->
             <div id="collapseTwo9" class="collapse" role="tabpanel" aria-labelledby="tabb-7" data-parent="#accordionEx">
                <div class="card-body px-0 py-3 pt-0  text-center">
                       <p>
                           <a class="title-manhag" href="https://dorar.net/article/1986">منهج العمل في الموسوعة</a>
                       </p>

                       <p class="subtitle-manhag">راجع الموسوعة</p>
                       <p>الأستاذُ صالحُ بنُ يوسُفَ المقرِن</p>
                       <p class="ustaz">باحثٌ في التَّاريخ الإسْلامِي والمُعاصِر
                           <br>
                           ومُشْرِفٌ تربَويٌّ سابقٌ بإدارة التَّعْليم</p>
                       <p>الأستاذُ الدُّكتور سعدُ بنُ موسى الموسى</p>
                       <p class="ustaz">أستاذُ التَّاريخِ الإسلاميِّ بجامعةِ أُمِّ القُرى</p>

                       <p>الدُّكتور خالِدُ بنُ محمَّد الغيث</p>
                       <p class="ustaz">أستاذُ التَّاريخِ الإسلاميِّ بجامعةِ أمِّ القُرى</p>

                       <p>الدُّكتور عبدُ اللهِ بنُ محمَّد علي حيدر</p>
                       <p class="ustaz">أستاذُ التَّاريخِ الإسلاميِّ بجامعةِ أمِّ القُرى</p>
                </div>
             </div>
          </div>
          <!-- Accordion card -->


          <!-- Accordion card -->
          <div class="card z-depth-0 mb-0">
            <!-- Card header -->
            <div class="card-header p-0" role="tab" id="tabb-8">
               <a class="collapsed" data-toggle="collapse" data-parent="#accordionEx" href="https://dorar.net/fake-hadith?page=3#collapseTwo10" aria-expanded="false" aria-controls="collapseTwo10">
                  <h6 class="h6-responsive mb-0 d-flex align-items-center justify-content-between b-0 py-3 px-1">
                     <div><i class="fa fa-file-o px-2" aria-hidden="true"></i> موسوعة اللغة العربية</div>
                     <i class="fa fa-angle-down rotate-icon px-3"></i>
                  </h6>
               </a>
            </div>
            <!-- Card body -->
            <div id="collapseTwo10" class="collapse" role="tabpanel" aria-labelledby="tabb-8" data-parent="#accordionEx">
               <div class="card-body px-0 py-3 pt-0 text-center">
                  <p>
                     <a class="title-manhag" href="https://dorar.net/article/2083">منهج العمل في الموسوعة</a>
                 </p>
                 <p class="ustaz">
                     تمَّ تحكيمُ موسوعةِ اللُّغةِ العربيَّةِ من<br>
                     <span>مكتبِ لغةِ المستقبلِ للاستشاراتِ اللغويَّةِ<br></span>

                     التابعِ لمعهدِ البحوثِ والاستشاراتِ اللغويَّةِ بـ<br>
                     <span></span>
                     جامعةِ الملكِ خالد بالسعوديَّةِ</p>
               </div>
            </div>
         </div>
         <!-- Accordion card -->


            </div>
            <!-- Accordion wrapper -->
      </div>
      <div class="modal-footer justify-content-center">

      </div>
   </div>
</div>
</div>

     
    
    <!--/. Sidebar navigation -->

    <!-- Start your project here -->
        <!-- Main layout -->
    <section>
        <div class="container-fluid">
            <div class="row justify-content-center">
                <div class="col-12 col-lg-11 col-xl-10 mb-3 mb-md-4 mb-lg-4">
                    <!-- Grid row -->
                    <div class="row">
                        <div class="col-12 mt-4">
                            <h1 class="h4-responsive default-text-color font-weight-bold my-0">
                                أحاديث منتشرة لا تصح
                            </h1>
                        </div>
                        <div class="col-12">
                            <nav aria-label="breadcrumb">
                                <ol class="breadcrumb bg-transparent mt-2 mb-1 px-0">
                                    <li class="breadcrumb-item">
                                        <a href="https://dorar.net/">الرئيسة</a>
                                    </li>
                                    <li class="breadcrumb-item active">أحاديث منتشرة لا تصح</li>
                                </ol>
                            </nav>
                        </div>
                        <div class="col-12 mb-2">
                            <div class="alert alert-dorar d-flex justify-content-between py-1 px-2">
                                <div class="d-flex align-items-center">
                                    <div class="d-lg-none d-xl-none d-flex align-items-end">
                                        <a href="https://dorar.net/feedback/suggest-hadith" class="btn btn-sm px-1 mx-1 btn-link btn-rounded d-flex align-items-center waves-effect waves-light">
                                            اقترح حديثا</a>
                                    </div>
                                    <!--/Dropdown primary-->
                                    <a href="https://dorar.net/feedback/suggest-hadith" class="btn btn-link btn-rounded btn-sm px-1 mx-1 d-none d-lg-inline-flex d-xl-inline-flex d-xl-none waves-effect waves-light">اقترح
                                        حديثا</a>
                                </div>
                                <div class="d-flex align-items-center">
                                    <div class="btn-group dorar-btn-group btn-sm border btn-rounded z-depth-2 d-none d-lg-inline-flex d-xl-inline-flex d-xl-none p-0 m-1" role="group" aria-label="Basic example">
                                        <button tag="1.06" step="0.01" type="button" class="btn btn-link btn-sm btn-rounded m-0 waves-effect waves-light font-btn">+</button>
                                        <button id="font-controler" tag="1.05" step="0.0" fnt="1.05" type="button" class="btn btn-link btn-sm btn-rounded px-1 m-0 waves-effect waves-light font-btn">
                                            <img src="./أحاديث منتشرة لا تصح_files/font-size.svg" alt="font-size" width="18">
                                        </button>
                                        <button tag="1.04" step="-0.01" type="button" class="btn btn-link btn-sm btn-rounded m-0 waves-effect waves-light font-btn">-</button>
                                    </div>
                                    <button type="button" class="tashkeel-btn change_tahshkeel_text_btn toggle_btn btn btn-default btn-sm btn-rounded d-none d-lg-inline-flex d-xl-inline-flex d-xl-none mx-1 actv waves-effect waves-light">تشكيل</button>
                                    <!--/Dropdown primary-->
                                </div>
                            </div>
                        </div>

                        <!-- Search form -->
<form role="search" id="inner-search" class="col-12" action="https://dorar.net/fake-hadith" method="get">
	<div class="row">
		<div class="col-10 col-sm-10 col-md-10 col-lg-5 col-xl-5 mb-4">
			<div class="input-group form-sm form-2">
				<input name="q" id="skeys" value="" class="form-control form-control-lg my-0 py-1 rounded z-depth-3 border-0" type="text" placeholder="كلمات البحث .." aria-label="Search">
				<div class="input-group-append mx-1">
					<button class="input-group-text custom_input_group_tbutton_1 bg-transparent border-0">
						<i class="fa fa-search default-text-light" aria-hidden="true"></i>
					</button>
					<button id="button-play-ws-inner" class="input-group-text custom_input_group_tbutton_2 bg-transparent border-0">
						<i class="fa fa-microphone default-text-light" aria-hidden="true"></i>
					</button>
				</div>
				<div class="input-group-append">
					<button class="btn btn-white z-depth-3 m-0 py-2 px-3 z-depth-0 waves-effect rounded" type="button" data-toggle="collapse" data-target="#multiCollapseExample10" aria-expanded="true" aria-controls="multiCollapseExample10">
						<i class="fa fa-gear fa-lg default-text-color" aria-hidden="true"></i>
					</button>
				</div>
			</div>
		</div>
		<div class="col-2 col-lg-7 mb-4 align-items-center d-flex justify-content-end">
            		</div>
		<!-- Collapsible content -->
		<div class="col-12">
			<div class="alert alert-danger alert-dismissible fade show mb-4" role="alert">
				<strong>
					راجعها واعتمد الحُكم عليها المشرف العام
				<button type="button" class="close" data-dismiss="alert" aria-label="Close">
					<span aria-hidden="true">×</span>
				</button>
			</strong></div><strong>
		</strong></div><strong>
		<div class="col-12 mb-4 collapse" id="multiCollapseExample10">
			<div class="card card-body px-2 dorar_advsearch_bg">
				<!-- form id="hist-form" role="form" class="form-group col-lg-12 pad-top10 mar-bot0"  action="/history/search" -->
				<div class="form-row pt-2">
					<div class="col-12 px-lg-3">
							<label class="mb-0 default-text-color"> طريقة البحث</label>
						</div>
						<div class="col-lg-6 pt-1 px-lg-3">
						<div class="select-wrapper mdb-select md-form my-0"><span class="caret">▼</span><input type="text" class="select-dropdown form-control" readonly="true" required="false" data-activates="select-options-st" value="" role="listbox" aria-multiselectable="false" aria-disabled="false" aria-required="false" aria-haspopup="true" aria-expanded="false"><ul id="select-options-st" class="dropdown-content select-dropdown w-100 " style="display: none;"><li class="  " role="option" aria-selected="false" aria-disabled="false"><span class="filtrable "> أي كلمة    </span></li><li class=" active " role="option" aria-selected="true" aria-disabled="false"><span class="filtrable "> جميع الكلمات    </span></li><li class="  " role="option" aria-selected="false" aria-disabled="false"><span class="filtrable "> بحث مطابق    </span></li></ul><select class="mdb-select md-form my-0 initialized" name="st" id="st">
														<option value="a">أي كلمة</option>
							<option selected="&quot;selected&quot;" value="w">جميع الكلمات</option>
							<option value="p">بحث مطابق</option>
						</select></div>
					</div>
					<div class="col-md-6 md-form mt-0 px-md-3">
						<!-- xclude -->
						<input type="text" class="form-control" placeholder="النتائج لا تحتوي هذه الكلمات" name="xclude" value="">
					</div>

					<div class="col-md-6 d-flex align-items-center my-1 px-md-3">
						<!-- Material unchecked -->
						<div class="form-check">
							<input name="fillopts" type="checkbox" class="form-check-input" id="materialUnchecked">
							<label class="form-check-label" for="materialUnchecked">تثبيت خيارات البحث
							</label>
						</div>
					</div>
					<div class="col-12 mt-2 px-md-3">
						<button type="submit" class="btn btn-default btn-rounded btn-sm mb-0 waves-effect waves-light">
							<i class="fa fa-search px-1"></i>
							بحث</button>
					</div>
				</div>
				<!-- /form -->
			</div>
		</div>
	</strong></div><strong>


                        <div class="col-12">
                            <!--/ Collapsible content -->
                            <div class="navbar-expand-sm sticky-top d-none d-lg-block d-xl-block d-xl-none dorar_sticky-top">
                                <div class="position-absolute">
                                    <ul class="list-unstyled text-center dorar_v_menu">
                                        <li class="my-3">
                                            <a href="https://dorar.net/fake-hadith?page=3#" title="مشاركة" class="default-text-color" data-toggle="modal" data-target="#modalSocial">
                                                <i class="fa-lg fa fa-share-alt"></i>
                                            </a>
                                        </li>
                                        <li class="my-3">
                                            <a href="https://dorar.net/fake-hadith?page=3#" data-toggle="tooltip" title="" class="default-text-color" onclick="window.print();" data-original-title="طباعة">
                                                <i class="fa-lg fa fa-print"></i>
                                            </a>
                                        </li>
                                        <li class="my-3">
                                            <a href="https://dorar.net/feedback/error-report?link=https://dorar.net/fake-hadith" data-toggle="tooltip" title="" class="default-text-color" data-original-title="نبه عن خطأ">
                                                <i class="fa-lg fa fa-warning"></i>
                                            </a>
                                        </li>
                                    </ul>
                                </div>
                            </div>
                            <div class="card card-personal z-depth-4 animated fadeIn h-100 rounded">
                                <div class="card-body" id="cntnt">
                                    <div class="w-100 mt-4 amiri_custom_content">
                                        
                                                                                                                            <article class="border-bottom py-4">
		<h5 class="h5-responsive edit" data-name="hadith" data-type="textarea" data-pk="31">
		31 - حديث: ((إذا أردتَ أمرًا فعليك بالتؤدة، حتى يريك الله منه مخرجًا)).</h5>
			<div class="d-block mb-2">
		<strong class="px-2">
			الدرجة:
			<span class="primary-text-color edit" data-name="degree" data-type="text" data-pk="31">ضعيف</span>
		</strong>
        
		
	</div>
	
    <a tag="31" href="https://dorar.net/fake-hadith/31" class="btn btn-outline-grey px-3 btn-sm shareLink a-btn waves-effect waves-light" data-toggle="modal" data-target="#modalSocial">
        <i class="fa fa-share-alt "></i>
    </a>
	<a href="https://dorar.net/feedback/error-report?link=https://dorar.net/fake-hadith" class="btn btn-outline-grey px-3 btn-sm a-btn waves-effect waves-light" title="اخبر عن خطأ">
		<i class="fa fa-exclamation-triangle"></i>
	</a>
	<a href="https://dorar.net/fake-hadith/31" class="btn btn-outline-grey px-3 btn-sm a-btn waves-effect waves-light" title="عرض الحديث">
		<i class="fa fa-external-link-square"></i>
	</a>
    

</article>
                                                                                                                                <article class="border-bottom py-4">
		<h5 class="h5-responsive edit" data-name="hadith" data-type="textarea" data-pk="32">
		32 - حديث: ((إذا رفع العبد يديه للسماء وهو عاصٍ فيقول: يا ربِّ، فتحجب الملائكة صوته، فيكرِّرها: يا ربِّ، فتحجب الملائكة صوته، فيكررها: يا ربِّ، فتحجب الملائكة صوته، فيكررها في الرابعة، فيقول الله عز وجل: إلى متى تحجبون صوت عبدي عني؟! لبيك عبدي، لبيك عبدي، لبيك عبدي، لبيك عبدي)).</h5>
			<div class="d-block mb-2">
		<strong class="px-2">
			الدرجة:
			<span class="primary-text-color edit" data-name="degree" data-type="text" data-pk="32">لا يصح</span>
		</strong>
        
		
	</div>
	
    <a tag="32" href="https://dorar.net/fake-hadith/32" class="btn btn-outline-grey px-3 btn-sm shareLink a-btn waves-effect waves-light" data-toggle="modal" data-target="#modalSocial">
        <i class="fa fa-share-alt "></i>
    </a>
	<a href="https://dorar.net/feedback/error-report?link=https://dorar.net/fake-hadith" class="btn btn-outline-grey px-3 btn-sm a-btn waves-effect waves-light" title="اخبر عن خطأ">
		<i class="fa fa-exclamation-triangle"></i>
	</a>
	<a href="https://dorar.net/fake-hadith/32" class="btn btn-outline-grey px-3 btn-sm a-btn waves-effect waves-light" title="عرض الحديث">
		<i class="fa fa-external-link-square"></i>
	</a>
    

</article>
                                                                                                                                <article class="border-bottom py-4">
		<h5 class="h5-responsive edit" data-name="hadith" data-type="textarea" data-pk="33">
		33 - حديث: ((إذا فتح الله عليكم مصر، فاتخذوا منها جندًا كثيفًا؛ فهم خير أجناد الأرض، وهم في رِباط إلى يوم الدين)).</h5>
			<div class="d-block mb-2">
		<strong class="px-2">
			الدرجة:
			<span class="primary-text-color edit" data-name="degree" data-type="text" data-pk="33">لا يصح</span>
		</strong>
        
		
	</div>
	
    <a tag="33" href="https://dorar.net/fake-hadith/33" class="btn btn-outline-grey px-3 btn-sm shareLink a-btn waves-effect waves-light" data-toggle="modal" data-target="#modalSocial">
        <i class="fa fa-share-alt "></i>
    </a>
	<a href="https://dorar.net/feedback/error-report?link=https://dorar.net/fake-hadith" class="btn btn-outline-grey px-3 btn-sm a-btn waves-effect waves-light" title="اخبر عن خطأ">
		<i class="fa fa-exclamation-triangle"></i>
	</a>
	<a href="https://dorar.net/fake-hadith/33" class="btn btn-outline-grey px-3 btn-sm a-btn waves-effect waves-light" title="عرض الحديث">
		<i class="fa fa-external-link-square"></i>
	</a>
    

</article>
                                                                                                                                <article class="border-bottom py-4">
		<h5 class="h5-responsive edit" data-name="hadith" data-type="textarea" data-pk="34">
		34 - حديث: ((إذا كان صيحة في رمضان، فإنها تكون معمعة في شوال، وتميز القبائل في ذي القعدة، وتسفك الدِّماء في ذي الحجة، والمحرم وما المحرم - يقولها ثلاثًا - هيهات هيهات، يقتل الناس فيها هرجًا هرجًا قال: قلنا: وما الصَّيحة يا رسول الله؟ قال: هذه تكون في نصف من رمضان، يوم جمعة ضحى، وذلك إذا وافق شهر رمضان ليلة الجمعة تكون هدة تُوقِظ النائم، وتُخرج العواتق من خُدورهن في ليلة جمعة، سَنَة كثيرة الزلازل والبرد، فإذا وافق رمضان في تلك السَّنة ليلة جمعة، فإذا صليتم الفجر يوم جُمُعة في النصف من رمضان، فادخلوا بيوتكم، وسدِّدوا كواكم، ودثِّروا أنفسكم، وسدُّوا آذانكم، فإذا أحسستم بالصيحة، فخِرُّوا لله سُجَّدًا، وقولوا سبحان القدوس، سبحان القدوس، ربنا القدوس؛ فإنَّه مَن فعل ذلك نجا، ومَن ترك هلك)).</h5>
			<div class="d-block mb-2">
		<strong class="px-2">
			الدرجة:
			<span class="primary-text-color edit" data-name="degree" data-type="text" data-pk="34"> كذبٌ موضوع</span>
		</strong>
        
		
	</div>
	
    <a tag="34" href="https://dorar.net/fake-hadith/34" class="btn btn-outline-grey px-3 btn-sm shareLink a-btn waves-effect waves-light" data-toggle="modal" data-target="#modalSocial">
        <i class="fa fa-share-alt "></i>
    </a>
	<a href="https://dorar.net/feedback/error-report?link=https://dorar.net/fake-hadith" class="btn btn-outline-grey px-3 btn-sm a-btn waves-effect waves-light" title="اخبر عن خطأ">
		<i class="fa fa-exclamation-triangle"></i>
	</a>
	<a href="https://dorar.net/fake-hadith/34" class="btn btn-outline-grey px-3 btn-sm a-btn waves-effect waves-light" title="عرض الحديث">
		<i class="fa fa-external-link-square"></i>
	</a>
    

</article>
                                                                                                                                <article class="border-bottom py-4">
		<h5 class="h5-responsive edit" data-name="hadith" data-type="textarea" data-pk="35">
		35 - حديث: ((إذا كان يومُ القيامة نادى منادٍ: يا محمد، قم فادخل الجنة بغير حساب، فيقوم كلُّ مَن كان اسمه محمد، ويتوهم أن النداء له، فلكرامة محمد لا يُمنعون)).</h5>
			<div class="d-block mb-2">
		<strong class="px-2">
			الدرجة:
			<span class="primary-text-color edit" data-name="degree" data-type="text" data-pk="35">موضوع</span>
		</strong>
        
		
	</div>
	
    <a tag="35" href="https://dorar.net/fake-hadith/35" class="btn btn-outline-grey px-3 btn-sm shareLink a-btn waves-effect waves-light" data-toggle="modal" data-target="#modalSocial">
        <i class="fa fa-share-alt "></i>
    </a>
	<a href="https://dorar.net/feedback/error-report?link=https://dorar.net/fake-hadith" class="btn btn-outline-grey px-3 btn-sm a-btn waves-effect waves-light" title="اخبر عن خطأ">
		<i class="fa fa-exclamation-triangle"></i>
	</a>
	<a href="https://dorar.net/fake-hadith/35" class="btn btn-outline-grey px-3 btn-sm a-btn waves-effect waves-light" title="عرض الحديث">
		<i class="fa fa-external-link-square"></i>
	</a>
    

</article>
                                                                                                                                <article class="border-bottom py-4">
		<h5 class="h5-responsive edit" data-name="hadith" data-type="textarea" data-pk="36">
		36 - حديث: أسماء بنت عميس: ((كان رسول الله صلى الله عليه وسلم يُوحَى إليه ورأسه في حجر علي، ولم يصلِّ العصر حتى غربت الشمس، فقال رسولُ الله صلى الله عليه وسلم لعلي: أصليتَ؟! قال: لا، قال رسول الله: اللهم إنه كان في طاعتك وطاعة رسولك، فارددْ عليه الشمس، قالت أسماء: فرأيتها غرَبت، ثم رأيتها طلعت بعدما غرَبت)).</h5>
			<div class="d-block mb-2">
		<strong class="px-2">
			الدرجة:
			<span class="primary-text-color edit" data-name="degree" data-type="text" data-pk="36">موضوع</span>
		</strong>
        		<strong class="px-2">
			<span class="text-danger ">|</span><a class="mr-2 text-info" target="_blank" href="https://dorar.net/fake-hadith/36?alts=1"><i class="fa fa-lg fa-link"></i> الصحيح البديل</a>
		</strong>
		
		
	</div>
	
    <a tag="36" href="https://dorar.net/fake-hadith/36" class="btn btn-outline-grey px-3 btn-sm shareLink a-btn waves-effect waves-light" data-toggle="modal" data-target="#modalSocial">
        <i class="fa fa-share-alt "></i>
    </a>
	<a href="https://dorar.net/feedback/error-report?link=https://dorar.net/fake-hadith" class="btn btn-outline-grey px-3 btn-sm a-btn waves-effect waves-light" title="اخبر عن خطأ">
		<i class="fa fa-exclamation-triangle"></i>
	</a>
	<a href="https://dorar.net/fake-hadith/36" class="btn btn-outline-grey px-3 btn-sm a-btn waves-effect waves-light" title="عرض الحديث">
		<i class="fa fa-external-link-square"></i>
	</a>
    

</article>
                                                                                                                                <article class="border-bottom py-4">
		<h5 class="h5-responsive edit" data-name="hadith" data-type="textarea" data-pk="37">
		37 - حديث: ((اطلب قلبك في ثلاثة مواضع: عند سماع القرآن، وفي مجالس الذِّكر، وفي أوقات الخَلوة، فإن لم تجده في هذه المواطن، فاسأل الله أن يعطيك قلبًا؛ فإنه لا قلب لك)).</h5>
			<div class="d-block mb-2">
		<strong class="px-2">
			الدرجة:
			<span class="primary-text-color edit" data-name="degree" data-type="text" data-pk="37">ليس بحديث، بل هو من كلام الإمام ابن القيِّم في كتابه ((الفوائد)) (1/149)</span>
		</strong>
        
		
	</div>
	
    <a tag="37" href="https://dorar.net/fake-hadith/37" class="btn btn-outline-grey px-3 btn-sm shareLink a-btn waves-effect waves-light" data-toggle="modal" data-target="#modalSocial">
        <i class="fa fa-share-alt "></i>
    </a>
	<a href="https://dorar.net/feedback/error-report?link=https://dorar.net/fake-hadith" class="btn btn-outline-grey px-3 btn-sm a-btn waves-effect waves-light" title="اخبر عن خطأ">
		<i class="fa fa-exclamation-triangle"></i>
	</a>
	<a href="https://dorar.net/fake-hadith/37" class="btn btn-outline-grey px-3 btn-sm a-btn waves-effect waves-light" title="عرض الحديث">
		<i class="fa fa-external-link-square"></i>
	</a>
    

</article>
                                                                                                                                <article class="border-bottom py-4">
		<h5 class="h5-responsive edit" data-name="hadith" data-type="textarea" data-pk="38">
		38 - حديث: ((اطلبوا العلم ولو بالصين)).</h5>
			<div class="d-block mb-2">
		<strong class="px-2">
			الدرجة:
			<span class="primary-text-color edit" data-name="degree" data-type="text" data-pk="38">لا يصح</span>
		</strong>
        
		
	</div>
	
    <a tag="38" href="https://dorar.net/fake-hadith/38" class="btn btn-outline-grey px-3 btn-sm shareLink a-btn waves-effect waves-light" data-toggle="modal" data-target="#modalSocial">
        <i class="fa fa-share-alt "></i>
    </a>
	<a href="https://dorar.net/feedback/error-report?link=https://dorar.net/fake-hadith" class="btn btn-outline-grey px-3 btn-sm a-btn waves-effect waves-light" title="اخبر عن خطأ">
		<i class="fa fa-exclamation-triangle"></i>
	</a>
	<a href="https://dorar.net/fake-hadith/38" class="btn btn-outline-grey px-3 btn-sm a-btn waves-effect waves-light" title="عرض الحديث">
		<i class="fa fa-external-link-square"></i>
	</a>
    

</article>
                                                                                                                                <article class="border-bottom py-4">
		<h5 class="h5-responsive edit" data-name="hadith" data-type="textarea" data-pk="39">
		39 - حديث: ((أكرموا عمَّتكم النخلة ؛ فإنها خُلقت من فَضلة طِينة آدم، وليس من الشجر شجرةٌ أكرم على الله من شجرة وُلدتْ تحتها مريم بنت عمران، فأطعموا نساءكم الوُلَّد الرُّطبَ؛ فإن لم يكن رطب فتمرٌ)).</h5>
			<div class="d-block mb-2">
		<strong class="px-2">
			الدرجة:
			<span class="primary-text-color edit" data-name="degree" data-type="text" data-pk="39">موضوع</span>
		</strong>
        		<strong class="px-2">
			<span class="text-danger ">|</span><a class="mr-2 text-info" target="_blank" href="https://dorar.net/fake-hadith/39?alts=1"><i class="fa fa-lg fa-link"></i> الصحيح البديل</a>
		</strong>
		
		
	</div>
	
    <a tag="39" href="https://dorar.net/fake-hadith/39" class="btn btn-outline-grey px-3 btn-sm shareLink a-btn waves-effect waves-light" data-toggle="modal" data-target="#modalSocial">
        <i class="fa fa-share-alt "></i>
    </a>
	<a href="https://dorar.net/feedback/error-report?link=https://dorar.net/fake-hadith" class="btn btn-outline-grey px-3 btn-sm a-btn waves-effect waves-light" title="اخبر عن خطأ">
		<i class="fa fa-exclamation-triangle"></i>
	</a>
	<a href="https://dorar.net/fake-hadith/39" class="btn btn-outline-grey px-3 btn-sm a-btn waves-effect waves-light" title="عرض الحديث">
		<i class="fa fa-external-link-square"></i>
	</a>
    

</article>
                                                                                                                                <article class="border-bottom py-4">
		<h5 class="h5-responsive edit" data-name="hadith" data-type="textarea" data-pk="40">
		40 - حديث: ((البُدلاء أربعون رجلًا، وأربعون امرأة، كلَّما مات رجل، أَبدل الله مكانه رجلًا، وكلما ماتت امرأة، أبدل الله مكانها امرأة)).</h5>
			<div class="d-block mb-2">
		<strong class="px-2">
			الدرجة:
			<span class="primary-text-color edit" data-name="degree" data-type="text" data-pk="40">موضوع</span>
		</strong>
        
		
	</div>
	
    <a tag="40" href="https://dorar.net/fake-hadith/40" class="btn btn-outline-grey px-3 btn-sm shareLink a-btn waves-effect waves-light" data-toggle="modal" data-target="#modalSocial">
        <i class="fa fa-share-alt "></i>
    </a>
	<a href="https://dorar.net/feedback/error-report?link=https://dorar.net/fake-hadith" class="btn btn-outline-grey px-3 btn-sm a-btn waves-effect waves-light" title="اخبر عن خطأ">
		<i class="fa fa-exclamation-triangle"></i>
	</a>
	<a href="https://dorar.net/fake-hadith/40" class="btn btn-outline-grey px-3 btn-sm a-btn waves-effect waves-light" title="عرض الحديث">
		<i class="fa fa-external-link-square"></i>
	</a>
    

</article>
                                                                                                                                <article class="border-bottom py-4">
		<h5 class="h5-responsive edit" data-name="hadith" data-type="textarea" data-pk="41">
		41 - حديث: ((البلاء موكل بالمنطق)).</h5>
			<div class="d-block mb-2">
		<strong class="px-2">
			الدرجة:
			<span class="primary-text-color edit" data-name="degree" data-type="text" data-pk="41">لا يصح، وصحح معناه ابن القيم في «تحفة المودود».</span>
		</strong>
        		<strong class="px-2">
			<span class="text-danger ">|</span><a class="mr-2 text-info" target="_blank" href="https://dorar.net/fake-hadith/41?alts=1"><i class="fa fa-lg fa-link"></i> الصحيح البديل</a>
		</strong>
		
		
	</div>
	
    <a tag="41" href="https://dorar.net/fake-hadith/41" class="btn btn-outline-grey px-3 btn-sm shareLink a-btn waves-effect waves-light" data-toggle="modal" data-target="#modalSocial">
        <i class="fa fa-share-alt "></i>
    </a>
	<a href="https://dorar.net/feedback/error-report?link=https://dorar.net/fake-hadith" class="btn btn-outline-grey px-3 btn-sm a-btn waves-effect waves-light" title="اخبر عن خطأ">
		<i class="fa fa-exclamation-triangle"></i>
	</a>
	<a href="https://dorar.net/fake-hadith/41" class="btn btn-outline-grey px-3 btn-sm a-btn waves-effect waves-light" title="عرض الحديث">
		<i class="fa fa-external-link-square"></i>
	</a>
    

</article>
                                                                                                                                <article class="border-bottom py-4">
		<h5 class="h5-responsive edit" data-name="hadith" data-type="textarea" data-pk="42">
		42 - حديث: ((التمس لأخيك بِضعًا وسبعين عُذرًا)).</h5>
			<div class="d-block mb-2">
		<strong class="px-2">
			الدرجة:
			<span class="primary-text-color edit" data-name="degree" data-type="text" data-pk="42">الحديث بهذا اللفظ لم نجدْه.</span>
		</strong>
        
		
	</div>
	
    <a tag="42" href="https://dorar.net/fake-hadith/42" class="btn btn-outline-grey px-3 btn-sm shareLink a-btn waves-effect waves-light" data-toggle="modal" data-target="#modalSocial">
        <i class="fa fa-share-alt "></i>
    </a>
	<a href="https://dorar.net/feedback/error-report?link=https://dorar.net/fake-hadith" class="btn btn-outline-grey px-3 btn-sm a-btn waves-effect waves-light" title="اخبر عن خطأ">
		<i class="fa fa-exclamation-triangle"></i>
	</a>
	<a href="https://dorar.net/fake-hadith/42" class="btn btn-outline-grey px-3 btn-sm a-btn waves-effect waves-light" title="عرض الحديث">
		<i class="fa fa-external-link-square"></i>
	</a>
    

</article>
                                                                                                                                <article class="border-bottom py-4">
		<h5 class="h5-responsive edit" data-name="hadith" data-type="textarea" data-pk="43">
		43 - حديث: ((الجنة تحت أقدام الأمهات)).<br>
وفي لفظ: ((الجنة تحت أقدام الأمهات، مَنْ شِئن أدخلن، ومَن شِئن أخرجن!))</h5>
			<div class="d-block mb-2">
		<strong class="px-2">
			الدرجة:
			<span class="primary-text-color edit" data-name="degree" data-type="text" data-pk="43">موضوع</span>
		</strong>
        		<strong class="px-2">
			<span class="text-danger ">|</span><a class="mr-2 text-info" target="_blank" href="https://dorar.net/fake-hadith/43?alts=1"><i class="fa fa-lg fa-link"></i> الصحيح البديل</a>
		</strong>
		
		
	</div>
	
    <a tag="43" href="https://dorar.net/fake-hadith/43" class="btn btn-outline-grey px-3 btn-sm shareLink a-btn waves-effect waves-light" data-toggle="modal" data-target="#modalSocial">
        <i class="fa fa-share-alt "></i>
    </a>
	<a href="https://dorar.net/feedback/error-report?link=https://dorar.net/fake-hadith" class="btn btn-outline-grey px-3 btn-sm a-btn waves-effect waves-light" title="اخبر عن خطأ">
		<i class="fa fa-exclamation-triangle"></i>
	</a>
	<a href="https://dorar.net/fake-hadith/43" class="btn btn-outline-grey px-3 btn-sm a-btn waves-effect waves-light" title="عرض الحديث">
		<i class="fa fa-external-link-square"></i>
	</a>
    

</article>
                                                                                                                                <article class="border-bottom py-4">
		<h5 class="h5-responsive edit" data-name="hadith" data-type="textarea" data-pk="44">
		44 - حديث: ((الحسن والحسين إمامان قامَا أو قعدَا)).</h5>
			<div class="d-block mb-2">
		<strong class="px-2">
			الدرجة:
			<span class="primary-text-color edit" data-name="degree" data-type="text" data-pk="44">ليس له وجود في كتب الحديث، وهو من وضْع الرافضة</span>
		</strong>
        
		
	</div>
	
    <a tag="44" href="https://dorar.net/fake-hadith/44" class="btn btn-outline-grey px-3 btn-sm shareLink a-btn waves-effect waves-light" data-toggle="modal" data-target="#modalSocial">
        <i class="fa fa-share-alt "></i>
    </a>
	<a href="https://dorar.net/feedback/error-report?link=https://dorar.net/fake-hadith" class="btn btn-outline-grey px-3 btn-sm a-btn waves-effect waves-light" title="اخبر عن خطأ">
		<i class="fa fa-exclamation-triangle"></i>
	</a>
	<a href="https://dorar.net/fake-hadith/44" class="btn btn-outline-grey px-3 btn-sm a-btn waves-effect waves-light" title="عرض الحديث">
		<i class="fa fa-external-link-square"></i>
	</a>
    

</article>
                                                                                                                                <article class="border-bottom py-4">
		<h5 class="h5-responsive edit" data-name="hadith" data-type="textarea" data-pk="45">
		45 - حديث: ((الحفظ في الصِّغر كالنقش في الحجر)).</h5>
			<div class="d-block mb-2">
		<strong class="px-2">
			الدرجة:
			<span class="primary-text-color edit" data-name="degree" data-type="text" data-pk="45">ليس بحديث</span>
		</strong>
        
		
	</div>
	
    <a tag="45" href="https://dorar.net/fake-hadith/45" class="btn btn-outline-grey px-3 btn-sm shareLink a-btn waves-effect waves-light" data-toggle="modal" data-target="#modalSocial">
        <i class="fa fa-share-alt "></i>
    </a>
	<a href="https://dorar.net/feedback/error-report?link=https://dorar.net/fake-hadith" class="btn btn-outline-grey px-3 btn-sm a-btn waves-effect waves-light" title="اخبر عن خطأ">
		<i class="fa fa-exclamation-triangle"></i>
	</a>
	<a href="https://dorar.net/fake-hadith/45" class="btn btn-outline-grey px-3 btn-sm a-btn waves-effect waves-light" title="عرض الحديث">
		<i class="fa fa-external-link-square"></i>
	</a>
    

</article>
                                                                                    
                                        <nav class="d-flex justify-content-center dorar_transparent_mode">
        <ul class="pagination pg-blue p-0">

                            <li class="page-item"> <a class="page-link waves-effect" href="https://dorar.net/fake-hadith?page=1" rel="first" aria-label="First page">«</a></li>
                <li class="page-item"> <a class="page-link waves-effect" href="https://dorar.net/fake-hadith?page=2" rel="prev" aria-label="Previous page">‹</a></li>
            
            

                                                                    <li class="page-item"><a class="page-link waves-effect" href="https://dorar.net/fake-hadith?page=1" aria-label="Page 1">1</a></li>
                                                                                                        <li class="page-item"><a class="page-link waves-effect" href="https://dorar.net/fake-hadith?page=2" aria-label="Page 2">2</a></li>
                                                                                                            <li class="page-item active"><a class="page-link waves-effect" aria-current="page" aria-label="Page 3">3</a></li>
                                                                                                        <li class="page-item"><a class="page-link waves-effect" href="https://dorar.net/fake-hadith?page=4" aria-label="Page 4">4</a></li>
                                                                                                        <li class="page-item"><a class="page-link waves-effect" href="https://dorar.net/fake-hadith?page=5" aria-label="Page 5">5</a></li>
                                                                                                        <li class="page-item"><a class="page-link waves-effect" href="https://dorar.net/fake-hadith?page=6" aria-label="Page 6">6</a></li>
                                                                                                        <li class="page-item"><a class="page-link waves-effect" href="https://dorar.net/fake-hadith?page=7" aria-label="Page 7">7</a></li>
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                


            



                            <li class="page-item"><a class="page-link waves-effect" href="https://dorar.net/fake-hadith?page=4" rel="next" aria-label="Next page">›</a></li>
                                        <li class="page-item"><a class="page-link waves-effect" href="https://dorar.net/fake-hadith?page=94" rel="last" aria-label="Last page">»</a></li>
                    </ul>
    </nav>

                                    </div>
                                </div>
                            </div>
                        </div>
                        <!-- Grid column -->
                    </strong></form></div><strong>
                </strong></div><strong>
            </strong></div><strong>
        </strong></div><strong>
    </strong></section><strong>
    <!-- Main layout -->

    <div class="modal fade" id="modalCommentForm" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
        <div class="modal-dialog cascading-modal" role="document">
            <!--Content-->
            <div class="modal-content">
                <!--Modal cascading tabs-->
                <div class="modal-c-tabs">
                    <!-- Nav tabs -->
                    <ul class="nav nav-tabs md-tabs tabs-2 darken-3 px-3 default-gradient" role="tablist">
                        <li class="nav-item waves-effect waves-light">
                            <a class="nav-link" data-toggle="tab" href="https://dorar.net/fake-hadith?page=3#panel7" role="tab">
                                <!-- <i class="fa fa-pencil-square-o mr-1"></i> -->
                                إضافة تعليق
                            </a>
                        </li>
                    </ul>
                    <!-- Tab panels -->
                    <div class="tab-content py-0">
                        <!--Panel 7-->
                        <form id="commentForm" action="https://dorar.net/fake-hadith?page=3" method="POST">
                            <input type="hidden" name="_token" value="EOcTKpAyeE1kTQSpsX7veXihkZfRaDHo5ETuMXOL" autocomplete="off">                            <input type="hidden" id="enc_id" name="enc_id" value="">
                            <input type="hidden" name="enc" value="hadith_spread">
                            <input type="hidden" name="title" value="">
                            <input type="hidden" name="url" value="">
                            <div class="tab-pane fade in show active" id="panel7" role="tabpanel">
                                <!--Body-->
                                <div class="modal-body mb-1 py-0">

                                    <div class="md-form form-sm mb-2">

                                        <i class="fa fa-pencil-square-o prefix active"></i>
                                        <textarea name="comment" class="md-textarea form-control" placeholder="إضافة تعليق" rows="3"></textarea>
                                    </div>
                                    <div class="text-center my-4">
                                        <button class="btn btn-default btn-sm btn-rounded waves-effect waves-light"> حفظ <i class="fa fa-paper-plane mx-1"></i></button>
                                    </div>
                                </div>
                                <!--Footer-->
                                <div class="modal-footer align-items-center p-3">
                                    <button type="submit" class="btn btn-outline-primary waves-effect mr-auto btn-sm" data-dismiss="modal">غلق</button>
                                </div>
                            </div>
                        </form>
                        <!--/.Panel 7-->
                    </div>
                </div>
            </div>
            <!--/.Content-->
        </div>
    </div>
    <!-- Footer -->
    <footer class="font-small mt-0 mx-0 px-0">
        <!-- Footer Links -->
        <div class="container-fluid">
            <div class="row justify-content-center">
                <div class="col-12 col-lg-11 col-xl-10 pt-4 border-top">
                    <!-- Grid row -->
                    <div class="row">
                        <!-- Grid column -->
                        <div class="col-lg-3 col-md-6 col-sm-6 col-6 mb-sm-0 mb-3">
                            <!-- Links -->
                            <h5 class="th5-responsive ext-uppercase default-text-color">روابط هامة </h5>
                            <ul class="list-unstyled">
                                <li>
                                    <a href="https://dorar.net/archive"><i class="fa fa-archive px-1"></i> الأرشيف
                                    </a>
                                </li>
                                
                                <li>
                                    <a href="https://dorar.net/feedback"><i class="fa fa-comments px-1"></i> راسلنا والأسئلة الشائعة</a>
                                </li>
                                <li>
                                    <a href="https://store.dorar.net/" target="_blank"><i class="fa fa-cart-arrow-down px-1"></i> المتجر</a>
                                </li>
                            </ul>
                        </div>
                        <!-- Grid column -->
                        <!-- <hr class="clearfix w-100 d-md-none py-0 mb-4 mt-0" /> -->
                        <!-- Grid column -->
                        <div class="col-lg-3 col-md-6 col-sm-6 col-6 mb-sm-0 mb-3">
                            <!-- Links -->
                            <h5 class="th5-responsive ext-uppercase default-text-color">خدمات تقنية</h5>
                            <ul class="list-unstyled">
                                <li>
                                    <a href="https://dorar.net/article/389"><i class="fa fa-code px-1"></i> خدمة API
                                    </a>
                                </li>
                                <li>
                                    <a href="https://dorar.net/article/2107"><i class="fa fa-search px-1"></i> نافذة البحث في الموسوعة الحديثية</a>
                                </li>
                                <li>
                                    <a href="https://dorar.net/gsearch"><i class="fa fa-globe px-1"></i> محرك بحث المواقع العلمية</a>
                                </li>
                            </ul>
                        </div>
                        <!-- Grid column -->
                        <!-- Grid column -->
                        <hr class="clearfix w-100 d-lg-none py-0 mb-4 mt-0">
                        <!-- Grid column -->
                        <div class="col-lg-3 col-md-12 mb-md-0 mb-3">
                            <!-- Links -->
                            <h5 class="th5-responsive ext-uppercase default-text-color">تابعنا</h5>
                            <div class="row">
                                <div class="col-6">
                                    <ul class="list-unstyled">
                                        <li>
                                            <a target="_blank" href="https://telegram.me/dorarnet_telegram" aria-label="تابعونا على تلجرام" class="li-ic d-lg-flex d-xl-flex align-xl-items-center align-lg-items-center"><i class="fa fa-telegram px-1 line-height-inherit"></i> Telegram</a>
                                        </li>
                                        <li>
                                            <a target="_blank" href="https://www.instagram.com/dorarrnet/" aria-label="تابعونا على إنستاجرام" class="ins-ic d-lg-flex d-xl-flex align-xl-items-center align-lg-items-center"><i class="fa fa-instagram px-1 line-height-inherit"></i> Instagram</a>
                                        </li>
                                        <li>
                                            <a target="_blank" href="https://whatsapp.com/channel/0029VaCfdUm8PgsLBK7VWm3G" aria-label="تابعونا على واتساب" class="whatsapp-ic d-lg-flex d-xl-flex align-xl-items-center align-lg-items-center"><i class="fa fa-whatsapp px-1 line-height-inherit"></i> Whatsapp</a>
                                        </li>
                                    </ul>
                                </div>
                                <div class="col-6">
                                    <ul class="list-unstyled">
                                        <li>
                                            <a target="_blank" href="https://www.facebook.com/pages/mwq-ldrr-lsny-Dorarnet/251615861901?ref=nf" aria-label="تابعونا على فيسبووك" class="fb-ic d-lg-flex d-xl-flex align-xl-items-center align-lg-items-center"><i class="fa fa-facebook px-1 line-height-inherit"></i> Facebook</a>
                                        </li>
                                        <li>
                                            <a target="_blank" href="https://x.com/dorarnet" aria-label="تابعونا على إكس" class="d-lg-flex d-xl-flex align-xl-items-center align-lg-items-center"><i class="fa icon-x-twitter px-1 line-height-inherit"></i> X
                                                platform</a>
                                        </li>
                                        <li>
                                            <a target="_blank" href="https://www.youtube.com/user/dorartv" aria-label="تابعونا على يوتيوب" class="yt-ic d-lg-flex d-xl-flex align-xl-items-center align-lg-items-center"><i class="fa fa-youtube px-1 line-height-inherit"></i> Youtube</a>
                                        </li>
                                    </ul>
                                </div>
                            </div>
                        </div>
                        <hr class="clearfix w-100 d-lg-none py-0 mb-4 mt-0">
                        <!-- Grid column -->
                        <div class="col-lg-3 col-md-12 mt-md-0 mt-3">
                            <!-- Content -->
                            <h5 class="th5-responsive ext-uppercase default-text-color">الاشتراك في القائمة البريدية
                            </h5>
                            <!-- Default form subscription -->
                            <form id="maillistForm" action="https://dorar.net/site/maillist" method="POST" class="mb-4 mt-3">
                                <input type="hidden" name="_token" value="EOcTKpAyeE1kTQSpsX7veXihkZfRaDHo5ETuMXOL" autocomplete="off">                                <!-- Name -->
                                <input type="email" id="mail" name="email" class="form-control mb-2 rounded" placeholder="بريدك الإلكتروني">
                                <strong id="mail-msg"></strong>
                                <!-- Sign in button -->
                                <div class="w-100 d-lg-flex flex-lg-row-reverse"><button id="join-maillist" class="maillist btn btn-sm btn-outline-default btn-rounded mx-0 waves-effect waves-light" type="submit" data-sitekey="6LfUShcqAAAAABtPg_dk30LSGBB2wy5Op4bRby9c" data-callback="onSubmit">أرسل</button></div>
                                <i id="loading" class="fa color-ae8422 fa-refresh fa-spin active"></i>

                            </form>
                            <!-- Default form subscription -->
                        </div>
                        <!-- Grid column -->
                    </div>
                    <!-- Grid row -->
                </div>
            </div>
        </div>
        <!-- Footer Links -->
        <div class="footer-copyright text-center py-3 default-gradient text-white">
            <p class="my-1 px-2">
                جميع الحقوق محفوظة لمؤسسة الدرر السنية 1421 هــ - 1448 هــ
            </p>
        </div>
        <!-- /.Copyright -->
    </footer>
    <!-- /.Footer -->
    <!--Modal: modalSocial-->
    <div class="modal fade" id="modalSocial" tabindex="-1" role="dialog" aria-labelledby="share-modal" aria-hidden="true">
        <div class="modal-dialog" role="document">

            <!--Content-->
            <div class="modal-content">

                <!--Header-->
                <div class="modal-header">
                    <h5 class="modal-title w-100" id="share-modal">انشر المادة</h5>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">×</span>
                    </button>
                </div>

                <!--Body-->
                <div class="modal-body mb-0 text-center">

                    <!--Facebook-->
                    <a target="_blank" tag="" id="fshare" href="https://www.facebook.com/sharer/sharer.php?u=https://dorar.net/fake-hadith" type="button" class="btn-floating btn-fb waves-effect waves-light" aria-label="مشاركة على فيسبووك"><i class="fa fa-facebook-f"></i></a>
                    <!--Twitter-->
                    <a target="_blank" tag="" id="tshare" href="http://twitter.com/share?url=https://dorar.net/fake-hadith" type="button" class="btn-floating btn-black waves-effect waves-light" aria-label="مشاركة على منصة إكس"><i class="fa icon-x-twitter"></i></a>
                    <!--Google +-->
                    <a target="_blank" id="wshare" href="https://api.whatsapp.com/send?text=https://dorar.net/fake-hadith" type="button" class="btn-floating btn-whatsapp waves-effect waves-light" aria-label="مشاركة على واتساب"><i class="fa fa-whatsapp"></i></a>
                    <!--telegram-->
                    <a target="_blank" id="tgshare" href="https://t.me/share/url?url=https://dorar.net/fake-hadith" type="button" class="btn-floating btn-li waves-effect waves-light" aria-label="مشاركة على تلجرام"><i class="fa fa-telegram"></i></a>

                    <div class="pt-2">
                        <!--copy-->
                        <!-- <a type="button" class="btn-floating btn-default"><i class="fa fa-copy"></i></a> -->

                        <input value="https://dorar.net/fake-hadith" id="copy-url" class="form-control text-left" type="text" readonly=""><br>
                                                <button data-clipboard-target="#copy-url" class="btn btn-outline-default btn-rounded btn-sm waves-effect waves-light cp-page-share" aria-label="نسخ"><i class="fa fa-copy px-1"></i>نسخ الرابط المختصر</button>
                                            </div>
                </div>

            </div>
            <!--/.Content-->

        </div>
    </div>
    <!--Modal: modalSocial-->

    <div class="modal fade amiri" id="modal1" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
        <div class="modal-dialog" role="document">
            <div class="modal-content">
                <div class="modal-header text-center">
                    <h4 class="h4-responsive modal-title w-100 font-weight-bold default-text-color">
                        لجنة الإشراف العلمي
                    </h4>
                    <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                        <span aria-hidden="true">×</span>
                    </button>
                </div>
                <div class="modal-body panel-body mx-3">
                    <div class="white z-depth-1 px-3 pt-3 pb-0">
                        <p class="text-center black-text">
                            تقوم اللجنة باعتماد منهجيات الموسوعات وقراءة
                            بعض مواد الموسوعات للتأكد من تطبيق المنهجية
                        </p>
                        <ul class="list-unstyled friend-list">
                            <li>
                                <span class="text-center">
                                    <div class="text-small">
                                        <strong class="font-size-16pt">الشيخ الدكتور هتلان بن علي
                                            الهتلان</strong>
                                        <p class="last-message text-muted ustaz p-2">قاضي بمحكمة الاستئناف بالدمام - سابقاً.</p>
                                    </div>
                                    <div class="chat-footer">
                                    </div>
                                </span>
                            </li>
                            <li>
                                <span class="text-center">
                                    <div class="text-small">
                                        <strong class="font-size-16pt">الشيخ الدكتور أسامة بن حسن
                                            الرتوعي</strong>
                                        <p class="last-message text-muted ustaz p-2">المستشار العلمي بمؤسسة الدرر
                                            السنية.</p>
                                    </div>
                                    <div class="chat-footer">
                                    </div>
                                </span>
                            </li>
                            <li>
                                <span class="text-center">
                                    <div class="text-small">
                                        <strong class="font-size-16pt">الشيخ الدكتور حسن بن علي
                                            البار</strong>
                                        <p class="last-message text-muted ustaz p-2">عضو الهيئة التعليمية بالكلية
                                            التقنية.</p>
                                    </div>
                                    <div class="chat-footer">
                                    </div>
                                </span>
                            </li>
                            <li>
                                <span class="text-center">
                                    <div class="text-small">
                                        <strong class="font-size-16pt">الشيخ الدكتور منصور بن حمد
                                            العيدي</strong>
                                        <p class="last-message text-muted ustaz p-2">الأستاذ بجامعة الإمام عبدالرحمن بن
                                            فيصل.</p>
                                    </div>
                                    <div class="chat-footer">
                                    </div>
                                </span>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <form id="social-login-form" action="https://dorar.net/fake-hadith?page=3" method="POST" class="d-none">
        <input type="hidden" name="_token" value="EOcTKpAyeE1kTQSpsX7veXihkZfRaDHo5ETuMXOL" autocomplete="off">
        <input id="social-login-access-token" name="social-login-access-token" type="text">
        <input id="social-login-tokenId" name="social-login-tokenId" type="text">
    </form>
            <!--Modal: Login / Register Form-->
        <div class="modal fade" id="modalLRForm" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
            <div class="modal-dialog cascading-modal" role="document">
                <!--Content-->
                <div class="modal-content">

                    

                    <!--Modal cascading tabs-->
                    <div id="tabs" class="modal-c-tabs">
                        <!-- Nav tabs -->
                        <ul class="nav nav-tabs md-tabs tabs-2 darken-3 px-3 default-gradient" role="tablist">
                            <li class="nav-item waves-effect waves-light">
                                <a class="nav-link active" data-toggle="tab" href="https://dorar.net/fake-hadith?page=3#panel7" role="tab">
                                    <i class="fa fa-user mx-1"></i>
                                    تسجيل الدخول</a>
                            </li>
                            <li class="nav-item waves-effect waves-light">
                                <a class="nav-link" data-toggle="tab" href="https://dorar.net/fake-hadith?page=3#panel8" role="tab">
                                    <i class="fa fa-user-plus mx-1"></i>
                                    تسجيل جديد</a>
                            </li>
                        </ul>
                        <!-- Tab panels -->
                        <div class="tab-content py-0">
                            <!--Panel 7-->
                            <div class="tab-pane fade in show active" id="panel7" role="tabpanel">
                                <!--Body-->
                                <div class="modal-body mb-1 py-0">
                                    <form id="loginForm" method="POST" action="https://dorar.net/member/login">
                                        <input type="hidden" name="_token" value="EOcTKpAyeE1kTQSpsX7veXihkZfRaDHo5ETuMXOL" autocomplete="off">                                        <div class="md-form form-sm mb-2">
                                            <i class="fa fa-envelope prefix active" aria-hidden="true"></i>
                                            <input name="email" id="email" class="form-control form-control-sm" type="text" placeholder="البريد الالكتروني" aria-label="Search">
                                        </div>
                                        <div class="md-form form-sm mb-2">
                                            <i class="fa fa-lock prefix active" aria-hidden="true"></i>
                                            <input name="password" id="password" class="form-control form-control-sm" type="password" placeholder="كلمة المرور" aria-label="Search">
                                                <div class="invalid-feedback" id="password_error"></div>
                                        </div>
                                        <div class="text-center mt-4 mb-2">
                                            <button type="submit" id="loginSubmitBtn" class="btn btn-default btn-sm btn-rounded waves-effect waves-light"><i class="fa fa-sign-in mx-1"></i> تسجيل الدخول </button>

                                        </div>
                                    </form>
                                    <p class="font-small text-right d-flex justify-content-center mb-3 pt-2"> أو يمكنك
                                        التسجيل من خلال</p>
                                    <div class="row my-3 d-flex justify-content-center">
                                        <!--Facebook-->
                                        <button type="button" class="btn btn-fb btn-sm btn-rounded social-login-btn waves-effect waves-light" data-provider="facebook"><i class="fa fa-facebook"></i></button>
                                        <!--Twitter-->
                                        <button type="button" class="btn btn-tw btn-sm btn-rounded twitter-icon social-login-btn waves-effect waves-light" data-provider="twitter"><i class="fa icon-x-twitter"></i></button>
                                        <!--Google +-->
                                        <button type="button" class="btn btn-google btn-sm btn-rounded social-login-btn waves-effect waves-light" data-provider="google"><i class="fa fa-google" aria-hidden="true"></i></button>
                                    </div>
                                </div>
                                <!--Footer-->
                                <div class="modal-footer align-items-center p-3">
                                    <div class="options text-right mt-1">
                                        <p class="mb-0">ليس لديك حساب؟ <a onclick="openTab()" href="https://dorar.net/fake-hadith?page=3#" class="green-text">إنشاء حساب جديد</a></p>
                                        <p class="mb-0">نسيت ؟<a href="https://dorar.net/member/password/reset" class="green-text">
                                                كلمة المرور </a></p>
                                    </div>
                                    <button type="button" class="btn btn-outline-primary waves-effect mr-auto btn-sm" data-dismiss="modal">غلق</button>
                                </div>
                            </div>
                            <!--/.Panel 7-->
                            <!--Panel 8-->
                            <div class="tab-pane fade" id="panel8" role="tabpanel">
                                <!--Body-->
                                <form id="registerForm" method="POST" action="https://dorar.net/member/register">
                                    <input type="hidden" name="_token" value="EOcTKpAyeE1kTQSpsX7veXihkZfRaDHo5ETuMXOL" autocomplete="off">                                    <div class="modal-body py-0">
                                        <div class="md-form form-sm mb-2">
                                            <i class="fa fa-envelope prefix active" aria-hidden="true"></i>
                                            <input name="register_name" id="register_name" class="form-control form-control-sm" type="text" placeholder="الاسم" aria-label="Search">
                                        </div>
                                        <div class="md-form form-sm mb-2">
                                            <i class="fa fa-envelope prefix active" aria-hidden="true"></i>
                                            <input name="register_email" id="register_email" class="form-control form-control-sm" type="text" placeholder="البريد الإلكتروني" aria-label="Search">
                                        </div>
                                        <div class="md-form form-sm mb-2">
                                            <i class="fa fa-lock prefix active" aria-hidden="true"></i>
                                            <input name="register_password" id="register_password" class="form-control form-control-sm" type="password" placeholder="كلمة المرور" aria-label="Search">
                                        </div>
                                        <div class="md-form form-sm mb-0">
                                            <i class="fa fa-lock prefix active" aria-hidden="true"></i>
                                            <input name="password_confirmation" id="password_confirmation" class="form-control form-control-sm" type="password" placeholder="إعادة كلمة المرور" aria-label="Search">
                                                <div class="invalid-feedback" id="register_password_error"></div>
                                        </div>

                                        <div class="text-center form-sm mt-4 mb-2">
                                            <button type="submit" class="btn btn-default btn-sm btn-rounded waves-effect waves-light" id="registerSubmitBtn"><i class="fa fa-sign-in mx-1"></i> إنشاء حساب جديد</button>
                                        </div>
                                        <p class="font-small text-right d-flex justify-content-center mb-3 pt-2"> أو يمكنك
                                            التسجيل من خلال</p>
                                        <div class="row my-3 d-flex justify-content-center">
                                            <!--Facebook-->
                                            <button type="button" class="btn btn-fb btn-sm btn-rounded social-login-btn waves-effect waves-light" data-provider="facebook"><i class="fa fa-facebook"></i></button>
                                            <!--Twitter-->
                                            <button type="button" class="btn btn-tw btn-sm btn-rounded twitter-icon social-login-btn waves-effect waves-light" data-provider="twitter"><i class="fa icon-x-twitter"></i></button>
                                            <!--Google +-->
                                            <button type="button" class="btn btn-sm btn-rounded social-login-btn waves-effect waves-light" data-provider="google"><i class="fa fa-google"></i></button>
                                        </div>
                                    </div>
                                </form>
                                <!--Footer-->
                                <div class="modal-footer align-items-center p-3">
                                    <div class="options text-right">
                                        <p class="pt-1 mb-0">لديك حساب ؟ <a class="green-text" href="https://dorar.net/fake-hadith?page=3#" onclick="openLoginTab()">تسجيل الدخول</a></p>
                                    </div>
                                    <button type="button" class="btn btn-outline-primary waves-effect mr-auto btn-sm" data-dismiss="modal">غلق</button>
                                </div>

                            </div>
                            <!--/.Panel 8-->
                        </div>
                    </div>
                </div>
                <!--/.Content-->
            </div>
        </div>
        <!--Modal: Login / Register Form-->
        <a id="back-to-top" href="https://dorar.net/fake-hadith?page=3#" class="btn-floating btn-md btn-white back-to-top waves-effect waves-light" role="button" aria-label="goto top" style=""><i class="fa fa-chevron-up default-text-color"></i></a>
    <!-- SCRIPTS -->
    
    <script data-pagespeed-no-defer="" src="./أحاديث منتشرة لا تصح_files/vendor.bundle.min.js"></script>
    
    <script src="./أحاديث منتشرة لا تصح_files/site.bundle.min.js"></script>
    

    <!-- Social Login Button Handler - Event Delegation -->
    <script>
        // Use event delegation to handle social login button clicks
        // This works even if buttons are in modals or added dynamically
        (function() {
            function handleSocialLoginClick(e) {
                // Check if clicked element is a social login button or inside one
                let button = e.target;

                // If clicked on icon or text inside button, find the button element
                while (button && !button.classList.contains('social-login-btn')) {
                    button = button.parentElement;
                    // Safety check to prevent infinite loop
                    if (!button || button === document.body) {
                        return;
                    }
                }

                if (button && button.classList.contains('social-login-btn')) {
                    e.preventDefault();
                    e.stopPropagation();
                    const provider = button.getAttribute('data-provider');
                    console.log('Social login button clicked:', provider);

                    // Disable button to prevent multiple clicks
                    button.disabled = true;
                    const originalText = button.innerHTML;
                    button.innerHTML = '<i class="fa fa-spinner fa-spin"></i>';

                    // Wait for socialSignin function to be available (with timeout)
                    let attempts = 0;
                    const maxAttempts = 100; // 10 seconds max wait (100 * 100ms)
                    let completed = false;

                    function trySocialSignin() {
                        if (completed) return;
                        attempts++;
                        if (window.socialSignin && typeof window.socialSignin === 'function' && window.firebaseAuthReady) {
                            console.log('socialSignin function is now available, calling with provider:', provider);
                            completed = true;
                            button.disabled = false;
                            button.innerHTML = originalText;
                            window.socialSignin(provider);
                        } else if (attempts < maxAttempts) {
                            // Try again in 100ms
                            setTimeout(trySocialSignin, 100);
                        } else {
                            // Timeout - function never loaded
                            completed = true;
                            console.error('socialSignin function failed to load after 10 seconds');
                            button.disabled = false;
                            button.innerHTML = originalText;
                            alert('فشل تحميل نظام تسجيل الدخول. يرجى تحديث الصفحة والمحاولة مرة أخرى.\nFailed to load login system. Please refresh the page and try again.');
                        }
                    }

                    // Also listen for the ready event for faster response
                    const readyHandler = function() {
                        if (!completed && window.socialSignin && typeof window.socialSignin === 'function') {
                            console.log('Firebase Auth ready event received, calling socialSignin');
                            completed = true;
                            button.disabled = false;
                            button.innerHTML = originalText;
                            window.socialSignin(provider);
                            window.removeEventListener('firebaseAuthReady', readyHandler);
                        }
                    };
                    window.addEventListener('firebaseAuthReady', readyHandler);

                    // Start trying immediately
                    trySocialSignin();
                }
            }

            // Attach event listener as soon as script loads
            if (document.readyState === 'loading') {
                document.addEventListener('DOMContentLoaded', function() {
                    document.addEventListener('click', handleSocialLoginClick);
                });
            } else {
                // DOM already loaded, attach immediately
                document.addEventListener('click', handleSocialLoginClick);
            }
        })();
    </script>

    <!-- Initializations -->
    
    
        <script src="./أحاديث منتشرة لا تصح_files/mainMember.js"></script>
    <script src="./أحاديث منتشرة لا تصح_files/fakehadith.js"></script>

    <script>
        function getFakeHadithSourceUrl(hadithId) {
            var origin = window.location.origin || 'https://dorar.net';
            var id = String(hadithId || '').trim();
            if (!id) {
                return window.location.href;
            }
            return origin + '/fake-hadith/' + id;
        }

        function appendSourceToCopiedText(text, hadithId) {
            var normalizedText = (text || '').trim();
            if (!normalizedText) {
                return '';
            }

            var sourceUrl = getFakeHadithSourceUrl(hadithId);
            if (!sourceUrl) {
                return normalizedText;
            }

            if (normalizedText.indexOf(sourceUrl) !== -1) {
                return normalizedText;
            }

            return normalizedText + '\n\nالمصدر: ' + sourceUrl;
        }

        function getSelectedFakeHadithId() {
            var selection = window.getSelection ? window.getSelection() : null;
            if (!selection || !selection.anchorNode) {
                return null;
            }

            var node = selection.anchorNode.nodeType === 1 ? selection.anchorNode : selection.anchorNode.parentElement;
            if (!node) {
                return null;
            }

            var article = node.closest ? node.closest('article') : null;
            if (!article) {
                return null;
            }

            var shareLink = article.querySelector('.shareLink[tag]');
            return shareLink ? shareLink.getAttribute('tag') : null;
        }

        // Append fake hadith URL when user copies selected page text
        document.addEventListener('copy', function(e) {
            var target = e.target;
            if (target && (target.tagName === 'INPUT' || target.tagName === 'TEXTAREA' || target.isContentEditable)) {
                return;
            }

            var selectedText = window.getSelection ? window.getSelection().toString().trim() : '';
            if (!selectedText) {
                return;
            }

            var selectedHadithId = getSelectedFakeHadithId();
            var textWithSource = appendSourceToCopiedText(selectedText, selectedHadithId);
            if (!textWithSource) {
                return;
            }

            e.preventDefault();
            if (e.clipboardData) {
                e.clipboardData.setData('text/plain', textWithSource);
            } else if (window.clipboardData) {
                window.clipboardData.setData('Text', textWithSource);
            }
        });

        // Handle social share with fake hadith text instead of URL
        let currentFakeHadithId = null;
        let currentFakeHadithText = null;

        // Handle share button click directly
        $(document).on('click', '.shareLink', function(e) {
            currentFakeHadithId = $(this).attr('tag');
            console.log('Fake hadith share button clicked, ID:', currentFakeHadithId);
        });

        // Override share button click to fetch fake hadith text
        $(document).on('show.bs.modal', '#modalSocial', function (e) {
            var button = $(e.relatedTarget);
            console.log('Modal opening, button:', button);

            // Get fake hadith ID from the share button's tag attribute
            if (button.hasClass('shareLink')) {
                currentFakeHadithId = button.attr('tag');
                console.log('Found ID from shareLink:', currentFakeHadithId);
            } else {
                // Fallback: find share link in the card
                var hadithCard = button.closest('.card');
                var shareLink = hadithCard.find('.shareLink').first();

                if (shareLink.length > 0) {
                    currentFakeHadithId = shareLink.attr('tag');
                    console.log('Found ID from card shareLink:', currentFakeHadithId);
                } else {
                    // Additional fallback: find any shareLink on the page
                    var anyShareLink = $('.shareLink').first();
                    if (anyShareLink.length > 0) {
                        currentFakeHadithId = anyShareLink.attr('tag');
                        console.log('Found ID from any shareLink:', currentFakeHadithId);
                    }
                }
            }

            if (currentFakeHadithId) {
                // Show loading state
                // $('#copy-url').val('جاري تحميل نص الحديث...');
                console.log('Fetching fake hadith text for ID:', currentFakeHadithId);

                // Fetch fake hadith text for copying
                $.ajax({
                    url: '/fake-hadith/' + currentFakeHadithId + '?copy=1',
                    method: 'GET',
                    dataType: 'json',
                    success: function(response) {
                        console.log('Fake hadith text response:', response);
                        currentFakeHadithText = response;
                        // Update the input field with fake hadith text preview (first 100 chars)
                        var preview = response.substring(0, 100) + (response.length > 100 ? '...' : '');
                        // $('#copy-url').val(preview);
                        console.log('Updated input field with preview:', preview);
                    },
                    error: function(xhr, status, error) {
                        console.error('Failed to fetch fake hadith text:', status, error);
                        // Try without JSON dataType as fallback
                        $.ajax({
                            url: '/fake-hadith/' + currentFakeHadithId + '?copy=1',
                            method: 'GET',
                            success: function(response) {
                                console.log('Fallback fake hadith text response:', response);
                                currentFakeHadithText = response;
                                var preview = response.substring(0, 100) + (response.length > 100 ? '...' : '');
                                // $('#copy-url').val(preview);
                            },
                            error: function() {
                                // $('#copy-url').val('حدث خطأ في تحميل نص الحديث');
                            }
                        });
                    }
                });
            } else {
                console.log('No fake hadith ID found');
            }
        });

        // Override social share links
        $(document).on('click', '#modalSocial #wshare', function(e) {
            e.preventDefault();
            if (currentFakeHadithText) {
                var whatsappUrl = 'https://api.whatsapp.com/send?text=' + encodeURIComponent(currentFakeHadithText);
                window.open(whatsappUrl, '_blank');
            }
        });

        $(document).on('click', '#modalSocial #tgshare', function(e) {
            e.preventDefault();
            if (currentFakeHadithText) {
                var telegramUrl = 'https://t.me/share/url?text=' + encodeURIComponent(currentFakeHadithText);
                window.open(telegramUrl, '_blank');
            }
        });

        $(document).on('click', '#modalSocial #fshare', function(e) {
            e.preventDefault();
            if (currentFakeHadithText) {
                // Facebook doesn't support direct text sharing, so we'll copy to clipboard and alert user
                navigator.clipboard.writeText(currentFakeHadithText).then(function() {
                    alert('تم نسخ النص إلى الحافظة. يمكنك الآن لصقه في منشور فيسبوك.');
                }).catch(function() {
                    // Fallback for older browsers
                    var tempInput = document.createElement('textarea');
                    tempInput.value = currentFakeHadithText;
                    document.body.appendChild(tempInput);
                    tempInput.select();
                    document.execCommand('copy');
                    document.body.removeChild(tempInput);
                    alert('تم نسخ النص إلى الحافظة. يمكنك الآن لصقه في منشور فيسبوك.');
                });
            }
        });

        $(document).on('click', '#modalSocial #tshare', function(e) {
            e.preventDefault();
            if (currentFakeHadithText) {
                // Twitter has character limit, so we'll copy to clipboard and alert user
                navigator.clipboard.writeText(currentFakeHadithText).then(function() {
                    alert('تم نسخ النص إلى الحافظة. يمكنك الآن لصقه في تغريدة.');
                }).catch(function() {
                    var tempInput = document.createElement('textarea');
                    tempInput.value = currentFakeHadithText;
                    document.body.appendChild(tempInput);
                    tempInput.select();
                    document.execCommand('copy');
                    document.body.removeChild(tempInput);
                    alert('تم نسخ النص إلى الحافظة. يمكنك الآن لصقه في تغريدة.');
                });
            }
        });

        // Update copy button to use fake hadith text
        $(document).on('click', '#modalSocial .cp-page-share', function(e) {
            if (currentFakeHadithText) {
                e.preventDefault();
                var textToCopy = appendSourceToCopiedText(currentFakeHadithText, currentFakeHadithId);
                navigator.clipboard.writeText(textToCopy).then(function() {
                    alert('تم نسخ الحديث بنجاح');
                }).catch(function() {
                    var tempInput = document.createElement('textarea');
                    tempInput.value = textToCopy;
                    document.body.appendChild(tempInput);
                    tempInput.select();
                    document.execCommand('copy');
                    document.body.removeChild(tempInput);
                    alert('تم نسخ الحديث بنجاح');
                });
            }
        });
    </script>
    <script>
        $(".AIRequest").on("click", function(e) {
            //e.preventDefault();

            var pt = $(this).attr('pt');
            if (pt == '14') {
                $('#generateKeyword').hide();
                $('#loading-image-keywords').show();
            } else if (pt == '2') {
                $('#generateQA').hide();
                $('#loading-image-QA').show();
            } else {
                $('#generateSEO').hide();
                $('#loading-image').show();
            }
            jQuery.ajax({
                type: "POST",
                url: '/seo/generate?pt=' + pt,
                data: {
                    text: "",
                    title: "",
                },
                success: function(response) {
                    //jQuery('#SEOTitle').val(response.title);
                    var seo = '';
                    //console.log(response);
                    var pt;
                    for (var i = 0; i < response.length; i++) {
                        console.log(response[i].content);
                        pt = response[0].prompt_id;
                        if (pt != 14)
                            seo = removeTags(response[i].content);
                        else
                            seo = response[i].content;

                    }

                    if (response[0].prompt_id == 14) {
                        jQuery('#SEOkeywords').html(seo);
                    } else if (response[0].prompt_id == 2) {
                        jQuery('#SEOQA').html(seo);
                    } else
                        jQuery('#SEODescription').html(seo);
                    // jQuery('#SEOkeywords').val(response.keywords);
                },
                complete: function() {
                    $('#loading-image').hide();
                    $('#loading-image-keywords').hide();
                    $('#loading-image-QA').hide();
                    $('#generateSEO').show();
                    $('#generateKeyword').show();
                    $('#generateQA').show();
                }
            });

        });

        function removeTags(text) {
            return text.replace(/(<([^>]+)>)/gi, '');
        }

    </script>
    
    
    
    <script type="module">
        import {
            initializeApp
        } from "https://www.gstatic.com/firebasejs/11.0.2/firebase-app.js";
        import {
            getAuth,
            signInWithPopup,
            FacebookAuthProvider,
            GoogleAuthProvider,
            TwitterAuthProvider,
            onAuthStateChanged,
            signInWithCustomToken,
            getIdToken
        } from "https://www.gstatic.com/firebasejs/11.0.2/firebase-auth.js";

        const firebaseConfig = {
            apiKey: "AIzaSyBGVfI-kNGzF-AzKOCL1AZYTeu5GWmTyTA",
            authDomain: "dorar-7e680.firebaseapp.com",
            databaseURL: "https://dorar-7e680.firebaseio.com",
            projectId: "dorar-7e680",
            storageBucket: "dorar-7e680.firebasestorage.app",
            messagingSenderId: "1018082870856",
            appId: "1:1018082870856:web:4940c9e5092082677e3a7d",
            measurementId: "G-6YY0VFQNWV"
        };

        const app = initializeApp(firebaseConfig);
        const auth = getAuth(app);

        const facebookProvider = new FacebookAuthProvider();
        const googleProvider = new GoogleAuthProvider();
        const twitterProvider = new TwitterAuthProvider();

        // Signal that socialSignin is ready
        window.firebaseAuthReady = true;
        console.log('Firebase Auth initialized - socialSignin function is now available');

        // Dispatch custom event to notify that Firebase is ready
        window.dispatchEvent(new CustomEvent('firebaseAuthReady'));

        window.socialSignin = async function(provider) {
            if (!auth) {
                console.error('Firebase auth is not initialized');
                alert('Firebase غير مهيأ بشكل صحيح. يرجى المحاولة مرة أخرى.');
                return;
            }

            let socialProvider = null;
            if (provider === "facebook") {
                socialProvider = facebookProvider;
            } else if (provider === "google") {
                socialProvider = googleProvider;
            } else if (provider === "twitter") {
                socialProvider = twitterProvider;
            } else {
                return;
            }

            try {
                const result = await signInWithPopup(auth, socialProvider);
                const uid = result.user.uid;

                const idToken = await getIdToken(result.user);

                // Send the token to the Laravel backend for verification and login
                try {
                    const response = await fetch('/member/firebase/login', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                            'X-CSRF-TOKEN': document.querySelector('meta[name="csrf-token"]').getAttribute('content'),
                        },
                        body: JSON.stringify({ idToken }), // Send the ID token
                    });

                    const responseData = await response.json();

                    if (responseData.status === 'success' || responseData.status === true) {
                        // Successfully logged into Laravel
                        console.log("Logged into Laravel successfully.");
                        window.location.href = responseData.redirect_url || responseData.url || '/'; // Redirect to a protected page
                    } else {
                        console.error("Laravel login failed:", responseData.message);
                        alert("حدث خطأ أثناء تسجيل الدخول: " + (responseData.message || "يرجى المحاولة مرة أخرى"));
                    }
                } catch (error) {
                    console.error("Error sending ID token to Laravel:", error);
                    alert("حدث خطأ أثناء الاتصال بالخادم. يرجى المحاولة مرة أخرى.");
                }

                setUserIdInCookies(uid);
                // Update Google Tag Manager with UID
                updateGtagUID(uid);
            } catch (error) {
                console.error("Error during sign-in:", error);
                let errorMessage = "حدث خطأ أثناء تسجيل الدخول";

                // Provide more specific error messages
                if (error.code === 'auth/popup-closed-by-user') {
                    errorMessage = "تم إغلاق نافذة تسجيل الدخول. يرجى المحاولة مرة أخرى.";
                } else if (error.code === 'auth/popup-blocked') {
                    errorMessage = "تم حظر النافذة المنبثقة. يرجى السماح بالنوافذ المنبثقة لهذا الموقع.";
                } else if (error.code === 'auth/unauthorized-domain') {
                    errorMessage = "هذا المجال غير مصرح به. يرجى الاتصال بالدعم الفني.";
                } else if (error.code === 'auth/operation-not-allowed') {
                    errorMessage = "طريقة تسجيل الدخول هذه غير مفعلة. يرجى الاتصال بالدعم الفني.";
                } else if (error.message) {
                    errorMessage = error.message;
                }

                alert(errorMessage);
            }
        };


        // Listen to authentication state changes
        onAuthStateChanged(auth, async (user) => {
            //console.log("User state changed:", user);
            if (user) {
                const uid = user.uid;


                // Update Google Tag Manager with UID
                setUserIdInCookies(uid);
                updateGtagUID(uid);
            } else {
                // User is signed out
                let uid = getCookie("user_id");
                if (!uid) {
                    uid = "guest_" + Math.random().toString(36).substr(2, 9);
                    setUserIdInCookies(uid);
                }
                updateGtagUID(uid);
            }
        });

        // Get the login form
        const loginForm = document.getElementById('loginForm');

        // Add an event listener to handle form submission
        loginForm.addEventListener('submit', async (event) => {
            // Prevent the default form submission behavior
            event.preventDefault();
            const submitBtn = document.getElementById('loginSubmitBtn');
            submitBtn.disabled = true; // Disable the button

            // Get the email and password values from the form
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;
            const csrfToken = document.querySelector('meta[name="csrf-token"]').getAttribute('content');
            // Call the async loginUser function
            try {
                await window.loginUser(email, password, csrfToken);

            } catch (error) {
                console.error('Login error:', error);
            } finally {
                // Enable the submit button and hide the loader after the request completes
                submitBtn.disabled = false;
            }
        });

        window.loginUser = async function(email, password, csrfToken) {
            document.getElementById('password_error').innerHTML = "";
            document.getElementById('password_error').style.display = 'none';

            try {
                const response = await fetch('/member/login', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'X-CSRF-TOKEN': csrfToken,
                        'X-Requested-From': 'ajaxLogin',
                    },
                    body: JSON.stringify({
                        email,
                        password
                    }),
                });

                const result = await response.json();

                if (result.status) {
                    // Use the returned token to authenticate with Firebase
                    const idToken = result.token;

                    // Log in on the client-side with Firebase
                    const userCredential = await signInWithCustomToken(auth, idToken);
                    const user = userCredential.user;
                    console.log("Logged in as:", user.uid);

                    // Redirect to the desired page
                    window.location.href = result.url;
                } else {
                    // Handle login failure
                    document.getElementById('password_error').innerHTML = "اسم المستخدم او كلمة المرور غير صحيحة";
                    document.getElementById('password_error').style.display = 'block';
                    console.error('Login failed:', result);
                }
            } catch (error) {
                console.error('Error logging in:', error);
                document.getElementById('password_error').innerHTML = "حدث خطأ اثناء تسجيل الدخول الرجاء المحاولة مرة اخرى";
                document.getElementById('password_error').style.display = 'block';

            }
        }

        // Get the register form
        const registerForm = document.getElementById('registerForm');

        registerForm.addEventListener('submit', async (event) => {
            // Prevent the default form submission behavior
            event.preventDefault();
            const submitBtn = document.getElementById('registerSubmitBtn');
            submitBtn.disabled = true; // Disable the button

            // Get the email and password values from the form
            const username = document.getElementById('register_name').value;
            const email = document.getElementById('register_email').value;
            const password = document.getElementById('register_password').value;
            const password_confirmation = document.getElementById('password_confirmation').value;
            const csrfToken = document.querySelector('meta[name="csrf-token"]').getAttribute('content');

            // Call the async loginUser function
            try {
                if (password !== password_confirmation) {
                    document.getElementById('register_password_error').innerHTML = "كلمة المرور غير متطابقة";
                    document.getElementById('register_password_error').style.display = 'block';
                    return;
                }

                await window.registerUser(username, email, password, password_confirmation, csrfToken);

            } catch (error) {
                console.error('Login error:', error);
            } finally {
                // Enable the submit button and hide the loader after the request completes
                submitBtn.disabled = false;
            }
        });

        window.registerUser = async function (name, email, password, password_confirmation, csrfToken) {

            try {
                const response = await fetch('/member/register', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'X-CSRF-TOKEN': csrfToken,
                        'X-Requested-From': 'ajaxLogin',
                    },
                    body: JSON.stringify({
                        name,
                        email,
                        password,
                        password_confirmation
                    }),
                });

                const result = await response.json();

                if (result.status) {
                    // Use the returned token to authenticate with Firebase
                    const idToken = result.token;

                    // Log in on the client-side with Firebase
                    const userCredential = await signInWithCustomToken(auth, idToken);
                    const user = userCredential.user;
                    console.log("Logged in as:", user.uid);

                    // Redirect to the desired page
                    window.location.href = result.url;
                } else {
                    // Handle login failure
                    // if(result.email){
                    //     document.getElementById('register_email_error').innerHTML = result.msg;
                    // }else if(result.password){
                    //     document.getElementById('register_password_error').innerHTML = result.msg;
                    // }else{
                    //     document.getElementById('register_password_error').innerHTML = "اسم المستخدم او كلمة المرور غير صحيحة";
                    // }
                    document.getElementById('register_password_error').innerHTML = result.msg;
                    document.getElementById('register_password_error').style.display = 'block';
                    // console.error('Login failed:', result);
                }
            } catch (error) {
                console.log(error);
                // document.getElementById('register_password_error').innerHTML = "حدث خطأ اثناء تسجيل الدخول الرجاء المحاولة مرة اخرى";
                document.getElementById('register_password_error').innerHTML = result.msg;
                document.getElementById('register_password_error').style.display = 'block';
                // console.error('Login failed:', result);

            }
        };

        // دالة لحفظ user_id في Cookies
        function setUserIdInCookies(userId) {
            document.cookie = `user_id=${'$'}{userId}; path=/; max-age=${'$'}{60 * 60 * 24 * 365}; SameSite=Lax`;
        }

        // دالة جلب user_id من Cookies
        function getCookie(name) {
            const value = `; ${'$'}{document.cookie}`;
            const parts = value.split(`; ${'$'}{name}=`);
            if (parts.length === 2) return parts.pop().split(";").shift();
        }


    </script>

<script>
    document.addEventListener('DOMContentLoaded', function() {
        const darkModeToggle = document.querySelector('[data-switch-dark]');
        const nightIcon = document.querySelector('.icon-night');
        const dayIcon = document.querySelector('.icon-day');

        // Apply dark mode on page load if it was enabled
        if (localStorage.getItem('darkMode') === 'enabled') {
            document.body.classList.toggle('dark-mode');
            toggleImg();

            nightIcon.classList.add('d-none');
            dayIcon.classList.remove('d-none');

            // Trigger the same events that happen on click

            // Add any other dark mode styles that your site uses
        }else{
            nightIcon.classList.remove('d-none');
            dayIcon.classList.add('d-none');
        }

        // darkModeToggle.addEventListener('click', function(e) {
        //     e.preventDefault();
        //     console.log(document.body.classList.contains('dark-mode'));
        //     if (document.body.classList.contains('dark-mode')) {
        //         document.body.classList.toggle('dark-mode');
        //         // Switch to light mode
        //         localStorage.setItem('darkMode', 'disabled');

        //     } else {
        //         // Switch to dark mode
        //         document.body.classList.toggle('dark-mode');
        //         localStorage.setItem('darkMode', 'enabled');

        //     }
        //     toggleImg()
        // });
    });

    document.addEventListener('DOMContentLoaded', function() {
    var menuBtn = document.querySelector('.button-collapse2');
    var slideOut = document.getElementById('slide-out');
    if(menuBtn && slideOut) {
        menuBtn.addEventListener('click', function(e) {
            e.preventDefault();
            slideOut.classList.toggle('active');
        });
    }
});

        </script>


    
    
    
<script>(function(){function c(){var b=a.contentDocument||a.contentWindow.document;if(b){var d=b.createElement('script');d.innerHTML="window.__CF${'$'}cv${'$'}params={r:'a1f2cf32ff520b04',t:'MTc4NDcyNzEyNA=='};var a=document.createElement('script');a.src='/cdn-cgi/challenge-platform/scripts/jsd/main.js';document.getElementsByTagName('head')[0].appendChild(a);";b.getElementsByTagName('head')[0].appendChild(d)}}if(document.body){var a=document.createElement('iframe');a.height=1;a.width=1;a.style.position='absolute';a.style.top=0;a.style.left=0;a.style.border='none';a.style.visibility='hidden';document.body.appendChild(a);if('loading'!==document.readyState)c();else if(window.addEventListener)document.addEventListener('DOMContentLoaded',c);else{var e=document.onreadystatechange||function(){};document.onreadystatechange=function(b){e(b);'loading'!==document.readyState&&(document.onreadystatechange=e,c())}}}})();</script><script type="module" src="./أحاديث منتشرة لا تصح_files/v4513226cdae34746b4dedf0b4dfa099e1781791509496" integrity="sha512-ZE9pZaUXND66v380QUtch/5sE9tPFh2zg45pR2PB0CVkCtOREv2AJKkSidISWkysEuQ0EH8faUU5du78bx87UQ==" data-cf-beacon="{&quot;version&quot;:&quot;2024.11.0&quot;,&quot;token&quot;:&quot;c6e88003c6e3474493436631fe81d3a5&quot;,&quot;server_timing&quot;:{&quot;name&quot;:{&quot;cfCacheStatus&quot;:true,&quot;cfEdge&quot;:true,&quot;cfExtPri&quot;:true,&quot;cfL4&quot;:true,&quot;cfOrigin&quot;:true,&quot;cfSpeedBrain&quot;:true},&quot;location_startswith&quot;:null}}" crossorigin="anonymous"></script>



</strong><iframe height="1" width="1" style="position: absolute; top: 0px; left: 0px; border-width: medium; border-style: none; border-color: currentcolor; border-image: none; visibility: hidden;" src="./أحاديث منتشرة لا تصح_files/saved_resource.html"></iframe><div class="hiddendiv common"></div></body></html>
    """.trimIndent()
}