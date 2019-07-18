# financegrapher
A simple utility to drop in expense reports (csv files) expenses and display graphs

I made this utility because I wanted to start tracking my finances better but wanted to do it in a way where I wouldn't have to do much manual data entry, see nice charts, and also not use a ton of excel formulas.

I am able to download csv files for my monthly credit card statements from my credit cards online portal. I then go through each entry and tag the purchase with one of the following:

B = bars
R = restaurants
T = transportation
G = groceries
U = utilities
M = medical
O = shopping
L = education
A = recreation

As long as the csv row has a header labeled "Type" with a value that maps to one of the above values, and a header labeled "Amount" with the amount of the purchase, any other data you want to include beyond that is up to you.

This is currently a basic program that displays pie charts and gives info on amount of spending per category as well as an option to include income and how much of income was able to be saved that month.

I will most likely expand more on this later but it is a very easy to use starter kit for those interested.

# To utilize the program

Just drop any of your csv files in to the csv folder and start the python server using the included start script.

