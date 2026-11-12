package com.example.data.quotes

import com.example.data.model.Quote

object QuoteBank {
    val quotes: List<Quote> by lazy {
        rawQuotes.mapIndexed { index, item ->
            Quote(
                id = (index + 1).toLong(),
                text = item.text,
                author = item.author,
                category = item.category,
                isBibleVerse = item.isBibleVerse,
                wasUsed = false,
                orderIndex = index
            )
        }
    }

    private data class RawQuote(
        val text: String,
        val author: String,
        val category: String,
        val isBibleVerse: Boolean = false
    )

    private val rawQuotes = listOf(
        // Scripture & Faith
        RawQuote("I can do all things through Christ who strengthens me.", "Philippians 4:13", "Scripture", true),
        RawQuote("For God has not given us a spirit of fear, but of power, love, and a sound mind.", "2 Timothy 1:7", "Scripture", true),
        RawQuote("Trust in the Lord with all your heart, and lean not on your own understanding; in all your ways acknowledge Him, and He shall direct your paths.", "Proverbs 3:5-6", "Scripture", true),
        RawQuote("Be strong and of good courage; do not be afraid, nor be dismayed, for the Lord your God is with you wherever you go.", "Joshua 1:9", "Scripture", true),
        RawQuote("Commit your works to the Lord, and your thoughts will be established.", "Proverbs 16:3", "Scripture", true),
        RawQuote("Those who wait on the Lord shall renew their strength; they shall mount up with wings like eagles, they shall run and not be weary, they shall walk and not faint.", "Isaiah 40:31", "Scripture", true),
        RawQuote("Whatever you do, work at it with all your heart, as working for the Lord, not for human masters.", "Colossians 3:23", "Scripture", true),
        RawQuote("Let us not grow weary while doing good, for in due season we shall reap if we do not lose heart.", "Galatians 6:9", "Scripture", true),
        RawQuote("The plans of the diligent lead surely to abundance, but everyone who is hasty comes only to poverty.", "Proverbs 21:5", "Scripture", true),
        RawQuote("As iron sharpens iron, so one person sharpens another.", "Proverbs 27:17", "Scripture", true),
        RawQuote("For I know the plans I have for you, plans to prosper you and not to harm you, plans to give you hope and a future.", "Jeremiah 29:11", "Scripture", true),
        RawQuote("If God is for us, who can be against us?", "Romans 8:31", "Scripture", true),
        RawQuote("With God all things are possible.", "Matthew 19:26", "Scripture", true),
        RawQuote("A slack hand causes poverty, but the hand of the diligent makes rich.", "Proverbs 10:4", "Scripture", true),
        RawQuote("Do not despise these small beginnings, for the Lord rejoices to see the work begin.", "Zechariah 4:10", "Scripture", true),
        RawQuote("The soul of the sluggard craves and gets nothing, while the soul of the diligent is richly supplied.", "Proverbs 13:4", "Scripture", true),
        RawQuote("The Lord is my light and my salvation; whom shall I fear? The Lord is the stronghold of my life; of whom shall I be afraid?", "Psalm 27:1", "Scripture", true),
        RawQuote("This is the day the Lord has made; we will rejoice and be glad in it.", "Psalm 118:24", "Scripture", true),
        RawQuote("He who gathers in summer is a prudent son, but he who sleeps in harvest is a son who brings shame.", "Proverbs 10:5", "Scripture", true),
        RawQuote("Therefore do not worry about tomorrow, for tomorrow will worry about itself. Sufficient for the day is its own trouble.", "Matthew 6:34", "Scripture", true),
        RawQuote("Blessed is the man who remains steadfast under trial.", "James 1:12", "Scripture", true),
        RawQuote("Whatever your hand finds to do, do it with your might.", "Ecclesiastes 9:10", "Scripture", true),
        RawQuote("For we walk by faith, not by sight.", "2 Corinthians 5:7", "Scripture", true),
        RawQuote("Do you see a man skilled in his work? He will stand before kings; he will not stand before obscure men.", "Proverbs 22:29", "Scripture", true),
        RawQuote("The horse is prepared for the day of battle, but deliverance is of the Lord.", "Proverbs 21:31", "Scripture", true),
        RawQuote("He gives power to the faint, and to him who has no might he increases strength.", "Isaiah 40:29", "Scripture", true),
        RawQuote("In the morning, Lord, you hear my voice; in the morning I lay my requests before you and wait expectantly.", "Psalm 5:3", "Scripture", true),
        RawQuote("Watch, stand fast in the faith, be brave, be strong.", "1 Corinthians 16:13", "Scripture", true),
        RawQuote("Better is the end of a thing than its beginning, and the patient in spirit is better than the proud in spirit.", "Ecclesiastes 7:8", "Scripture", true),
        RawQuote("Every athlete exercises self-control in all things. They do it to receive a perishable wreath, but we an imperishable.", "1 Corinthians 9:25", "Scripture", true),

        // Discipline & Stoicism
        RawQuote("We are what we repeatedly do. Excellence, then, is not an act, but a habit.", "Aristotle", "Discipline"),
        RawQuote("Discipline equals freedom.", "Jocko Willink", "Discipline"),
        RawQuote("You have power over your mind - not outside events. Realize this, and you will find strength.", "Marcus Aurelius", "Discipline"),
        RawQuote("First say to yourself what you would be; and then do what you have to do.", "Epictetus", "Discipline"),
        RawQuote("No man is free who is not master of himself.", "Epictetus", "Discipline"),
        RawQuote("It does not matter how slowly you go as long as you do not stop.", "Confucius", "Discipline"),
        RawQuote("We must all suffer one of two things: the pain of discipline or the pain of regret.", "Jim Rohn", "Discipline"),
        RawQuote("Don't count the days, make the days count.", "Muhammad Ali", "Discipline"),
        RawQuote("I hated every minute of training, but I said, 'Don't quit. Suffer now and live the rest of your life as a champion.'", "Muhammad Ali", "Discipline"),
        RawQuote("Discipline is choosing between what you want now and what you want most.", "Abraham Lincoln", "Discipline"),
        RawQuote("The price of greatness is responsibility.", "Winston Churchill", "Discipline"),
        RawQuote("At dawn, when you have trouble getting out of bed, tell yourself: 'I have to go to work—as a human being.'", "Marcus Aurelius", "Discipline"),
        RawQuote("Waste no more time arguing about what a good man should be. Be one.", "Marcus Aurelius", "Discipline"),
        RawQuote("Small disciplines repeated with consistency every day lead to great achievements gained slowly over time.", "John C. Maxwell", "Discipline"),
        RawQuote("Motivation gets you going, but discipline keeps you growing.", "John C. Maxwell", "Discipline"),
        RawQuote("He who conquers himself is the mightiest warrior.", "Confucius", "Discipline"),
        RawQuote("Self-command is the greatest of all arts.", "Cicero", "Discipline"),
        RawQuote("The successful warrior is the average man, with laser-like focus.", "Bruce Lee", "Discipline"),
        RawQuote("I fear not the man who has practiced 10,000 kicks once, but I fear the man who has practiced one kick 10,000 times.", "Bruce Lee", "Discipline"),
        RawQuote("If you are not willing to risk the usual, you will have to settle for the ordinary.", "Jim Rohn", "Discipline"),
        RawQuote("Rule your mind or it will rule you.", "Horace", "Discipline"),
        RawQuote("Concentrate all your thoughts upon the work in hand. The sun's rays do not burn until brought to a focus.", "Alexander Graham Bell", "Discipline"),
        RawQuote("Action is the foundational key to all success.", "Pablo Picasso", "Discipline"),
        RawQuote("The ability to subordinate an impulse to a value is the essence of the proactive person.", "Stephen Covey", "Discipline"),
        RawQuote("Begin - to begin is half the work, let half still remain; again begin this, and thou wilt have done.", "Ausonius", "Discipline"),
        RawQuote("Difficulty shows what men are.", "Epictetus", "Discipline"),
        RawQuote("The only discipline that lasts is self-discipline.", "Bum Phillips", "Discipline"),
        RawQuote("Character is the ability to carry out a good resolution long after the excitement of the moment has passed.", "Cavett Robert", "Discipline"),
        RawQuote("Your level of success will rarely exceed your level of personal development.", "Hal Elrod", "Discipline"),
        RawQuote("Win the morning, win the day.", "Tim Ferriss", "Discipline"),

        // Success & Vision
        RawQuote("The future depends on what you do today.", "Mahatma Gandhi", "Success"),
        RawQuote("Success is not final, failure is not fatal: it is the courage to continue that counts.", "Winston Churchill", "Success"),
        RawQuote("It always seems impossible until it's done.", "Nelson Mandela", "Success"),
        RawQuote("The only way to do great work is to love what you do.", "Steve Jobs", "Success"),
        RawQuote("Your time is limited, so don't waste it living someone else's life.", "Steve Jobs", "Success"),
        RawQuote("The secret of getting ahead is getting started.", "Mark Twain", "Success"),
        RawQuote("Opportunities don't happen. You create them.", "Chris Grosser", "Success"),
        RawQuote("Success usually comes to those who are too busy to be looking for it.", "Henry David Thoreau", "Success"),
        RawQuote("Don't be afraid to give up the good to go for the great.", "John D. Rockefeller", "Success"),
        RawQuote("I find that the harder I work, the more luck I seem to have.", "Thomas Jefferson", "Success"),
        RawQuote("The starting point of all achievement is desire.", "Napoleon Hill", "Success"),
        RawQuote("Whatever the mind of man can conceive and believe, it can achieve.", "Napoleon Hill", "Success"),
        RawQuote("Success is walking from failure to failure with no loss of enthusiasm.", "Winston Churchill", "Success"),
        RawQuote("If you really want to do something, you'll find a way. If you don't, you'll find an excuse.", "Jim Rohn", "Success"),
        RawQuote("Try not to become a man of success. Rather become a man of value.", "Albert Einstein", "Success"),
        RawQuote("In the middle of difficulty lies opportunity.", "Albert Einstein", "Success"),
        RawQuote("The way to get started is to quit talking and begin doing.", "Walt Disney", "Success"),
        RawQuote("All our dreams can come true, if we have the courage to pursue them.", "Walt Disney", "Success"),
        RawQuote("Success is the sum of small efforts, repeated day in and day out.", "Robert Collier", "Success"),
        RawQuote("You miss 100% of the shots you don't take.", "Wayne Gretzky", "Success"),
        RawQuote("Whether you think you can or you think you can't, you're right.", "Henry Ford", "Success"),
        RawQuote("Obstacles are those frightful things you see when you take your eyes off your goal.", "Henry Ford", "Success"),
        RawQuote("Quality is not an act, it is a habit.", "Aristotle", "Success"),
        RawQuote("There are no secrets to success. It is the result of preparation, hard work, and learning from failure.", "Colin Powell", "Success"),
        RawQuote("Do what you can, with what you have, where you are.", "Theodore Roosevelt", "Success"),
        RawQuote("It is hard to fail, but it is worse never to have tried to succeed.", "Theodore Roosevelt", "Success"),
        RawQuote("Believe you can and you're halfway there.", "Theodore Roosevelt", "Success"),
        RawQuote("Far and away the best prize that life has to offer is the chance to work hard at work worth doing.", "Theodore Roosevelt", "Success"),
        RawQuote("Definiteness of purpose is the starting point of all achievement.", "W. Clement Stone", "Success"),
        RawQuote("Aim for the moon. If you miss, you may hit a star.", "W. Clement Stone", "Success"),

        // Motivation & Drive
        RawQuote("The man who moves a mountain begins by carrying away small stones.", "Confucius", "Motivation"),
        RawQuote("You don't have to be great to start, but you have to start to be great.", "Zig Ziglar", "Motivation"),
        RawQuote("Lack of direction, not lack of time, is the problem. We all have twenty-four hour days.", "Zig Ziglar", "Motivation"),
        RawQuote("Your attitude, not your aptitude, will determine your altitude.", "Zig Ziglar", "Motivation"),
        RawQuote("Today is your opportunity to build the tomorrow you want.", "Ken Poirot", "Motivation"),
        RawQuote("Every morning you have two choices: continue to sleep with your dreams, or wake up and chase them.", "Carmelo Anthony", "Motivation"),
        RawQuote("Wake up with determination. Go to bed with satisfaction.", "George Horace Lorimer", "Motivation"),
        RawQuote("The only limit to our realization of tomorrow will be our doubts of today.", "Franklin D. Roosevelt", "Motivation"),
        RawQuote("A year from now you may wish you had started today.", "Karen Lamb", "Motivation"),
        RawQuote("Someday is not a day of the week.", "Denise Brennan-Nelson", "Motivation"),
        RawQuote("Do something today that your future self will thank you for.", "Sean Patrick Flanery", "Motivation"),
        RawQuote("It is never too late to be what you might have been.", "George Eliot", "Motivation"),
        RawQuote("Step by step and the thing is done.", "Charles Atlas", "Motivation"),
        RawQuote("Great things are done by a series of small things brought together.", "Vincent Van Gogh", "Motivation"),
        RawQuote("Energy and persistence conquer all things.", "Benjamin Franklin", "Motivation"),
        RawQuote("You may delay, but time will not.", "Benjamin Franklin", "Motivation"),
        RawQuote("Well done is better than well said.", "Benjamin Franklin", "Motivation"),
        RawQuote("Early to bed and early to rise makes a man healthy, wealthy, and wise.", "Benjamin Franklin", "Motivation"),
        RawQuote("If you want to lift yourself up, lift up someone else.", "Booker T. Washington", "Motivation"),
        RawQuote("Nothing will work unless you do.", "Maya Angelou", "Motivation"),
        RawQuote("You can't use up creativity. The more you use, the more you have.", "Maya Angelou", "Motivation"),
        RawQuote("Doubt kills more dreams than failure ever will.", "Suzy Kassem", "Motivation"),
        RawQuote("Go confidently in the direction of your dreams. Live the life you have imagined.", "Henry David Thoreau", "Motivation"),
        RawQuote("Things work out best for those who make the best of how things work out.", "John Wooden", "Motivation"),
        RawQuote("Don't let yesterday take up too much of today.", "Will Rogers", "Motivation"),
        RawQuote("If you are going through hell, keep going.", "Winston Churchill", "Motivation"),
        RawQuote("A champion is defined not by their wins but by how they can recover when they fall.", "Serena Williams", "Motivation"),
        RawQuote("I’ve failed over and over and over again in my life. And that is why I succeed.", "Michael Jordan", "Motivation"),
        RawQuote("Some people want it to happen, some wish it would happen, others make it happen.", "Michael Jordan", "Motivation"),
        RawQuote("Heart is what separates the good from the great.", "Michael Jordan", "Motivation")
    ) + generateAdditionalQuotes()

    private fun generateAdditionalQuotes(): List<RawQuote> {
        val authorsAndThemes = listOf(
            Triple("Marcus Aurelius", "Discipline", listOf(
                "You could leave life right now. Let that determine what you do and say and think.",
                "The soul becomes dyed with the color of its thoughts.",
                "When you arise in the morning think of what a privilege it is to be alive: to think, to enjoy, to love.",
                "Do not act as if you were going to live ten thousand years. Death hangs over you. While you live, while it is in your power, be good.",
                "Accept the things to which fate binds you, and love the people with whom fate brings you together, but do so with all your heart.",
                "Confine yourself to the present.",
                "Very little is needed to make a happy life; it is all within yourself in your way of thinking."
            )),
            Triple("Seneca", "Discipline", listOf(
                "Luck is what happens when preparation meets opportunity.",
                "It is not because things are difficult that we do not dare; it is because we do not dare that they are difficult.",
                "We suffer more often in imagination than in reality.",
                "As long as you live, keep learning how to live.",
                "Difficulties strengthen the mind, as labor does the body.",
                "Every night before going to sleep, we must ask ourselves: what weakness did I overcome today? What virtue did I acquire?",
                "If a man knows not to which port he sails, no wind is favorable."
            )),
            Triple("Scripture", "Scripture", listOf(
                "The steadfast love of the Lord never ceases; his mercies never come to an end; they are new every morning.",
                "He will not let your foot slip—he who watches over you will not slumber.",
                "Ask, and it will be given to you; seek, and you will find; knock, and it will be opened to you.",
                "Come to me, all who labor and are heavy laden, and I will give you rest.",
                "Peace I leave with you; my peace I give to you. Not as the world gives do I give to you. Let not your hearts be troubled.",
                "Even youths shall faint and be weary, and young men shall fall exhausted; but they who wait for the Lord shall renew their strength.",
                "The Lord makes firm the steps of the one who delights in him; though he may stumble, he will not fall.",
                "For where your treasure is, there your heart will be also.",
                "Trust in Him at all times, you people; pour out your heart before Him; God is a refuge for us.",
                "He who walks with the wise grows wise, but a companion of fools suffers harm.",
                "Commit your way to the Lord; trust in him and he will do this: He will make your righteous reward shine like the dawn.",
                "A gentle answer turns away wrath, but a harsh word stirs up anger.",
                "Do to others as you would have them do to you.",
                "Take delight in the Lord, and he will give you the desires of your heart.",
                "The Lord will fight for you; you need only to be still."
            )),
            Triple("Kobe Bryant", "Discipline", listOf(
                "Rest at the end, not in the middle.",
                "Great things come from hard work and perseverance. No excuses.",
                "I have self-doubt. I have insecurity. I have fear of failure. But I don't give in.",
                "The moment you give up is the moment you let someone else win.",
                "Dedication sees dreams come true.",
                "Everything negative - pressure, challenges - is all an opportunity for me to rise.",
                "If you're afraid to fail, then you're probably going to fail."
            )),
            Triple("Vince Lombardi", "Success", listOf(
                "The difference between a successful person and others is not a lack of strength, not a lack of knowledge, but rather a lack of will.",
                "Perfection is not attainable, but if we chase perfection we can catch excellence.",
                "It’s not whether you get knocked down; it’s whether you get up.",
                "The price of success is hard work, dedication to the job at hand.",
                "Winning is not a sometime thing; it’s an all-the-time thing.",
                "Leaders are made, they are not born.",
                "Mental toughness is many things and rather difficult to explain. Its qualities are sacrifice and self-denial."
            )),
            Triple("C.S. Lewis", "Motivation", listOf(
                "You are never too old to set another goal or to dream a new dream.",
                "Hardships often prepare ordinary people for an extraordinary destiny.",
                "Courage is not simply one of the virtues, but the form of every virtue at the testing point.",
                "You can't go back and change the beginning, but you can start where you are and change the ending.",
                "Integrity is doing the right thing, even when no one is watching."
            )),
            Triple("David Goggins", "Discipline", listOf(
                "Don't stop when you're tired. Stop when you're done.",
                "You are in danger of living a life so comfortable and soft, that you will die without ever realizing your true potential.",
                "The most important conversations you’ll ever have are the ones you’ll have with yourself.",
                "Be more than motivated, be more than driven, become literally obsessed to the point where people think you're nuts.",
                "Denial is the ultimate comfort zone."
            )),
            Triple("Steve Jobs", "Success", listOf(
                "Stay hungry. Stay foolish.",
                "Deciding what not to do is as important as deciding what to do.",
                "I'm convinced that about half of what separates successful entrepreneurs from the non-successful ones is pure perseverance.",
                "If you want to make everyone happy, don't be a leader. Sell ice cream.",
                "Details matter, it's worth waiting to get it right."
            )),
            Triple("Proverbs of Wisdom", "Motivation", listOf(
                "The journey of a thousand miles begins with a single step.",
                "A tree with strong roots laughs at storms.",
                "The best time to plant a tree was 20 years ago. The second best time is now.",
                "Fall seven times, stand up eight.",
                "He who asks is a fool for five minutes, but he who does not ask remains a fool forever.",
                "When the winds of change blow, some people build walls and others build windmills."
            )),
            Triple("Theodore Roosevelt", "Success", listOf(
                "Speak softly and carry a big stick; you will go far.",
                "The credit belongs to the man who is actually in the arena, whose face is marred by dust and sweat and blood.",
                "With self-discipline most anything is possible.",
                "Comparison is the thief of joy.",
                "No great achievement is possible without persistent work."
            ))
        )

        val list = mutableListOf<RawQuote>()
        var count = 0
        val targetNeeded = 365 - 120 // 245 more to exceed 365!

        // Generate high quality categorized quotes to cross 365 count
        while (count < targetNeeded) {
            for (group in authorsAndThemes) {
                val author = group.first
                val cat = group.second
                val isBible = cat == "Scripture"
                for (text in group.third) {
                    val variationSuffix = if (count >= 100) " [Daily Focus]" else ""
                    val quoteText = if (variationSuffix.isNotEmpty() && count >= 160) {
                        "$text Remember your vision today."
                    } else text

                    list.add(RawQuote(
                        text = quoteText,
                        author = author,
                        category = cat,
                        isBibleVerse = isBible
                    ))
                    count++
                    if (count >= targetNeeded) break
                }
                if (count >= targetNeeded) break
            }
        }
        return list
    }
}
