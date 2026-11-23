package com.example.myapplication

object AppStrings {
    fun get(key: String, lang: String): String {
        val isChichewa = lang == "Chichewa"
        return when (key) {
            // Bottom Nav
            "nav_dashboard" -> if (isChichewa) "Poyambira" else "Dashboard"
            "nav_crops" -> if (isChichewa) "Mbewu" else "Crops"
            "nav_finance" -> if (isChichewa) "Ndalama" else "Finance"
            "nav_weather" -> if (isChichewa) "Nyengo" else "Weather"
            "nav_profile" -> if (isChichewa) "Mbiri" else "Profile"

            // Dashboard
            "quick_actions" -> if (isChichewa) "Zochita Mwachangu" else "Quick Actions"
            "season_expenses" -> if (isChichewa) "Ndalama Zowononga" else "Season Expenses"
            "inventory_stock" -> if (isChichewa) "Katundu wa M'sitolo" else "Inventory Stock"
            "total_expenses" -> if (isChichewa) "Zonse zowononga" else "Total recorded expenses"
            "total_items" -> if (isChichewa) "Zinthu zonse m'sitolo" else "Total items in stock"
            "smart_modules" -> if (isChichewa) "Gawo la Mindayi Yanzeru & Zina" else "Smart Farm Modules & Proposal Features"
            "search_hint" -> if (isChichewa) "Sakani mbewu, zida, munda..." else "Search crops, expenses, or inventory..."
            "greeting" -> if (isChichewa) "Moni," else "Hello,"
            "dashboard_subtitle" -> if (isChichewa) "Munda wanu uli bwino lero. Nazi chidule cha zomwe zili paminda." else "Your farm is performing optimally today. Here is your daily summary."

            // Quick Actions buttons
            "btn_add_crop" -> if (isChichewa) "Lowetsani Mbewu" else "Add Crop"
            "btn_record_activity" -> if (isChichewa) "Lembani Ntchito" else "Record Activity"
            "btn_log_harvest" -> if (isChichewa) "Lembani Zokolola" else "Log Harvest"

            // Modules
            "mod_crops" -> if (isChichewa) "Kusamalira Mbewu & Mitundu" else "Crops & Variety Management"
            "mod_inventory" -> if (isChichewa) "Katundu & Zida za M'sitolo" else "Inventory Stock & Equipment"
            "mod_expenses" -> if (isChichewa) "Ndalama & Zowononga" else "Expenses & Finance"
            "mod_harvests" -> if (isChichewa) "Zokolola & Kugulitsa" else "Harvest & Sales Records"
            "mod_farms" -> if (isChichewa) "Mindayi" else "Farms & Land Plots"
            "mod_weather" -> if (isChichewa) "Zidziwitso za Nyengo" else "Weather Advisory"
            "mod_reports" -> if (isChichewa) "Malipoti a Ndalama" else "Financial Reports & Analysis"
            "mod_activities" -> if (isChichewa) "Ntchito za M'munda & Kupopera" else "Farm Activities & Spraying"
            "mod_market" -> if (isChichewa) "Mitengo ya Pamsika" else "Local Market Prices"
            "mod_reminders" -> if (isChichewa) "Zikumbutso" else "Reminders & Alerts"

            // Module Subtitles
            "sub_crops" -> if (isChichewa) "Onani nthawi yakukula, kudyetsa & thanzi la mbewu" else "Track growth stages, planting dates & crop health"
            "sub_inventory" -> if (isChichewa) "Yang'anirani fetereza, njovu, ndi zida" else "Manage fertilisers, seeds, tools & equipment stock"
            "sub_expenses" -> if (isChichewa) "Onani ndalama zowonongedwa pa munda" else "Monitor production costs, labour & agricultural inputs"
            "sub_harvests" -> if (isChichewa) "Lembani zokolola ndi ndalama zopezera" else "Log harvest yields, market prices & buyer sales"
            "sub_farms" -> if (isChichewa) "Onani minda yanu yonse" else "Manage acreage, soil types & land allocation"
            "sub_weather" -> if (isChichewa) "Onani nyengo yozizira kapena yotentha" else "Rainfall forecasts, temperature & farming advisories"
            "sub_reports" -> if (isChichewa) "Onani phindu ndi kuwonongeka" else "Profitability, yield trends & financial analytics"
            "sub_activities" -> if (isChichewa) "Lembani kupopera, kulima & kuthira fetereza" else "Spraying schedules, weeding & fertilizer application"
            "sub_market" -> if (isChichewa) "Onani mitengo ya Admarc ndi misika" else "Current produce pricing across regional markets"
            "sub_reminders" -> if (isChichewa) "Zikumbutso za munda" else "Farming tasks, alerts & seasonal notifications"

            else -> key
        }
    }
}
