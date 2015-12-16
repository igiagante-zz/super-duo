# super-duo

## Alexandria

...* If one book is deleted, the list of books is updated.
When the user deletes a digit from the isbn, the last search is still there. It doesn’t need to call every time to the book service.
The list of books permit now searching over all the books. It can detect when the user is entering a letter and then, it used to filter the list of books at the moment.
If the book doesn’t have any image to show, an image will be shown saying that the image is not available.
The search’s function was changed for a button. I think it’s not a good idea to ask to the service every time the user enter 13 digits.
It shouldn’t add a book to the database if the user hasn’t confirmed.
The back button from book detail was removed.
All the UI was improved. Now you can appreciate a new style.
It saves the last isbns, so when the user tries to search again a book, it will be recommended those isbns which match with the entered numbers.
When the user click the button search and there is a result for the search, the keyboard disappears.


## Soccer App

The app was rebuilt from scratch.
The app uses Material Design with Tablayout.
The matches are divided by league for one day. There are not mixed.
If there isn’t any match for one day, it will show an message saying that.
The data retrieved by the soccer service are persisted only one time.
After one week the table Match is cleaned. This avoid increasing a lot the database.
The first time the app is launched, the team service will retrieve all the teams and persist them. While the process is running, a progress dialog is shown with the message “Updating teams”.
It uses the library Glide to load correctly the team’s images.
The layout was inspired by the soccer app: https://play.google.com/store/apps/details?id=com.mobilefootie.wc2010&hl=en
An Algorithm was implemented in order to change the svg path images to png path. It’s easier and cleaner to work with png than svg.
The images and text have content descriptions.
Support RTL was added.
A widget collection was added in order to inform about the matches which will be played during the day. In case there isn’t any match for the day, it will show a message saying that.

