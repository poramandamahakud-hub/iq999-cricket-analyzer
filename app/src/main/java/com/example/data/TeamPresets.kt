package com.example.data

import com.example.model.CricketTeamPreset

object TeamPresets {
    val PRESET_TEAMS = listOf(
        CricketTeamPreset(
            id = "ind",
            name = "India",
            shortCode = "IND",
            category = "International",
            primaryColorHex = "#1E40AF",
            keyBatters = listOf("Rohit Sharma", "Virat Kohli", "Suryakumar Yadav", "Rishabh Pant", "Hardik Pandya"),
            keyBowlers = listOf("Jasprit Bumrah", "Kuldeep Yadav", "Arshdeep Singh", "Mohammed Siraj", "Axar Patel"),
            defaultStrengths = listOf("World-class death bowling by Bumrah", "Elite middle-overs spin attack", "Dynamic powerplay intent"),
            defaultWeaknesses = listOf("Top-order left-arm fast incoming swing vulnerability", "Occasional lower-order tail collapse under pressure", "Lack of sixth bowling option in certain XI combinations")
        ),
        CricketTeamPreset(
            id = "aus",
            name = "Australia",
            shortCode = "AUS",
            category = "International",
            primaryColorHex = "#EAB308",
            keyBatters = listOf("Travis Head", "Mitchell Marsh", "Glenn Maxwell", "Marcus Stoinis", "Josh Inglis"),
            keyBowlers = listOf("Mitchell Starc", "Pat Cummins", "Josh Hazlewood", "Adam Zampa", "Nathan Ellis"),
            defaultStrengths = listOf("Relentless pace battery with elite seam movement", "High boundary-hitting frequency in powerplay", "Aggressive clutch match mindset"),
            defaultWeaknesses = listOf("High dot-ball percentage against quality wrist-spin", "Death overs yorker inconsistency under heavy pressure", "Rigid top-order collapse against disciplined finger spin")
        ),
        CricketTeamPreset(
            id = "eng",
            name = "England",
            shortCode = "ENG",
            category = "International",
            primaryColorHex = "#DC2626",
            keyBatters = listOf("Jos Buttler", "Phil Salt", "Harry Brook", "Liam Livingstone", "Will Jacks"),
            keyBowlers = listOf("Jofra Archer", "Adil Rashid", "Reece Topley", "Mark Wood", "Sam Curran"),
            defaultStrengths = listOf("Uncompromising ultra-aggressive batting depth", "Elite leg-spin variations from Adil Rashid", "Fast pace variations in powerplay"),
            defaultWeaknesses = listOf("High risk of sudden multi-wicket batting collapses against movement", "Excessive boundary concession when pace bowlers miss yorker lengths", "Weakness against hard length short-pitched strategy")
        ),
        CricketTeamPreset(
            id = "sa",
            name = "South Africa",
            shortCode = "SA",
            category = "International",
            primaryColorHex = "#15803D",
            keyBatters = listOf("Heinrich Klaasen", "Quinton de Kock", "Aiden Markram", "David Miller", "Tristan Stubbs"),
            keyBowlers = listOf("Kagiso Rabada", "Anrich Nortje", "Keshav Maharaj", "Marco Jansen", "Tabraiz Shamsi"),
            defaultStrengths = listOf("Brutal middle-overs spin bashing by Klaasen & Miller", "High pace options with steep bounce", "Exceptional outfield boundary catching"),
            defaultWeaknesses = listOf("High vulnerability against slow low turning tracks", "Pressure panic in tight final-over chases", "Death overs bowling extras and boundary leaks")
        ),
        CricketTeamPreset(
            id = "pak",
            name = "Pakistan",
            shortCode = "PAK",
            category = "International",
            primaryColorHex = "#047857",
            keyBatters = listOf("Babar Azam", "Mohammad Rizwan", "Fakhar Zaman", "Saim Ayub", "Iftikhar Ahmed"),
            keyBowlers = listOf("Shaheen Afridi", "Naseem Shah", "Haris Rauf", "Shadab Khan", "Abrar Ahmed"),
            defaultStrengths = listOf("Deadly early powerplay swing with Shaheen & Naseem", "Explosive pace through Haris Rauf in middle overs", "Individual match-winning brilliance"),
            defaultWeaknesses = listOf("Low powerplay strike rate causing middle-overs squeeze", "Frequent fielding lapses and high dropped catch ratio", "Spin bowling discipline failure in pressure death overs")
        ),
        CricketTeamPreset(
            id = "nz",
            name = "New Zealand",
            shortCode = "NZ",
            category = "International",
            primaryColorHex = "#171717",
            keyBatters = listOf("Rachin Ravindra", "Daryl Mitchell", "Devon Conway", "Glenn Phillips", "Kane Williamson"),
            keyBowlers = listOf("Trent Boult", "Matt Henry", "Mitchell Santner", "Lockie Ferguson", "Ish Sodhi"),
            defaultStrengths = listOf("Laser-accurate seam discipline and tactical flexibility", "Elite outfield catching and run-out execution", "Consistent middle-overs rotation"),
            defaultWeaknesses = listOf("Lack of genuine 150+ km/h death bowling impact", "Inability to hit consistent 200+ totals on flat highways", "Susceptibility to heavy high-pace short bowling")
        ),
        CricketTeamPreset(
            id = "mi",
            name = "Mumbai Indians",
            shortCode = "MI",
            category = "T20 League",
            primaryColorHex = "#2563EB",
            keyBatters = listOf("Rohit Sharma", "Suryakumar Yadav", "Ishan Kishan", "Hardik Pandya", "Tilak Varma"),
            keyBowlers = listOf("Jasprit Bumrah", "Gerald Coetzee", "Piyush Chawla", "Naman Dhir", "Akash Madhwal"),
            defaultStrengths = listOf("Bumrah's untouchable death bowling economy", "Unrivalled 360-degree boundary hitting by Suryakumar", "Heavy powerplay hitters"),
            defaultWeaknesses = listOf("Secondary pace options leaking 12+ runs per over", "Spin department depth vulnerability on flat pitches", "Frequent early wickets lost against left-arm angle")
        ),
        CricketTeamPreset(
            id = "csk",
            name = "Chennai Super Kings",
            shortCode = "CSK",
            category = "T20 League",
            primaryColorHex = "#CA8A04",
            keyBatters = listOf("Ruturaj Gaikwad", "Shivam Dube", "Rachin Ravindra", "MS Dhoni", "Ravindra Jadeja"),
            keyBowlers = listOf("Matheesha Pathirana", "Ravindra Jadeja", "Mustafizur Rahman", "Deepak Chahar", "Tushar Deshpande"),
            defaultStrengths = listOf("Tactical spin choke at home spin grounds", "Shivam Dube's devastating spin destruction in middle overs", "Pathirana's lethal low-arm slinging yorkers"),
            defaultWeaknesses = listOf("Slower top-order powerplay starts on fast tracks", "High ground fielding vulnerability against rapid runners", "Susceptibility of top order against high express pace above 145 km/h")
        ),
        CricketTeamPreset(
            id = "rcb",
            name = "Royal Challengers Bengaluru",
            shortCode = "RCB",
            category = "T20 League",
            primaryColorHex = "#B91C1C",
            keyBatters = listOf("Virat Kohli", "Rajat Patidar", "Will Jacks", "Cameron Green", "Dinesh Karthik"),
            keyBowlers = listOf("Mohammed Siraj", "Yash Dayal", "Lockie Ferguson", "Karn Sharma", "Swapnil Singh"),
            defaultStrengths = listOf("Virat Kohli's relentless anchor-to-accelerate masterclasses", "Destructive middle-order hitting against pace", "Aggressive powerplay bowling intent"),
            defaultWeaknesses = listOf("High boundary concession rate at death overs", "Spin attack leaking runs in middle overs", "Over-reliance on top 3 batters")
        ),
        CricketTeamPreset(
            id = "kkr",
            name = "Kolkata Knight Riders",
            shortCode = "KKR",
            category = "T20 League",
            primaryColorHex = "#7E22CE",
            keyBatters = listOf("Sunil Narine", "Phil Salt", "Shreyas Iyer", "Rinku Singh", "Andre Russell"),
            keyBowlers = listOf("Sunil Narine", "Varun Chakaravarthy", "Mitchell Starc", "Harshit Rana", "Vaibhav Arora"),
            defaultStrengths = listOf("Mystery spin duo Narine & Varun choking middle overs", "Andre Russell & Rinku Singh match-winning death hitting", "Powerplay pinch-hitting intent"),
            defaultWeaknesses = listOf("High risk top-order collapse on seam-friendly tracks", "Starc's expensive overs when missing yorkers", "Vulnerability against hard short ball body line")
        )
    )

    fun getPresetByName(name: String): CricketTeamPreset? {
        return PRESET_TEAMS.find { it.name.equals(name, ignoreCase = true) || it.shortCode.equals(name, ignoreCase = true) }
    }
}
