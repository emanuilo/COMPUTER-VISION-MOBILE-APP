# COMPUTER-VISION-MOBILE-APP
Application for purposes of museums and galleries on Android devices.  
Main feature is paintings detection through the 
camera vision and showing an additional content about the painting in the camera view.  

Additional content:
* Information about detected painting
* Button for more of related paintings
* Button for a video about the painting

Consisted of 3 applications:  
* Android user app - capturing paintings and showing an additional content
* Android admin app - adding new paintings and all additional content to the database. 
* Desktop server app - mediator for a communication between the database, user and admin app.

Used technologies:
* OpenCV 
* REST Web Services
* Hibernate
* PostgreSQL
* Glide

<br/>

## CLIENT APPLICATION
### Features
* Detecting the painting on a wall
* Showing an additional content in the camera view based on detected painting
  * Painting name, artist, short description
  * Button for more of related images
  * Button for a video about the painting
  * Longer HTML formated description by clicking on the short one


![1](https://user-images.githubusercontent.com/18516460/69900301-caa0f600-1371-11ea-8995-ae60164213de.png)
![2](https://user-images.githubusercontent.com/18516460/69900302-cb398c80-1371-11ea-9dbc-0520f9e6d71e.png)
![3](https://user-images.githubusercontent.com/18516460/69900303-cb398c80-1371-11ea-8b24-b3a8ba9a572a.png)
![4](https://user-images.githubusercontent.com/18516460/69900304-cb398c80-1371-11ea-9c09-76ab14c4621c.png)
![5](https://user-images.githubusercontent.com/18516460/69900305-cbd22300-1371-11ea-853c-8cc0b0f9d7ec.png)

<br/>
 
## ADMIN APPLICATION
### Features
* Adding new paintings to the database
  * Detecting a painting on a wall
  * Cutting the photo around the painting edges
  * Perspective transform if the painting is captured by the angle
  * Adding all the information about the painting
* Managing the library of inserted paintings (editing, deleting)
* Managing exhibitions, attaching paintings to a specific exhibition


![1](https://user-images.githubusercontent.com/18516460/69900326-05a32980-1372-11ea-8b5e-85edd7c6bd9c.png)
![2](https://user-images.githubusercontent.com/18516460/69900327-05a32980-1372-11ea-90d2-5f1035aefd0a.png)
![3](https://user-images.githubusercontent.com/18516460/69900328-063bc000-1372-11ea-8b75-de8fae051b0a.png)
![4](https://user-images.githubusercontent.com/18516460/69900329-063bc000-1372-11ea-89b0-be6395deca51.png)
![5](https://user-images.githubusercontent.com/18516460/69900330-063bc000-1372-11ea-8a72-7178a5fad8d3.png)
![6](https://user-images.githubusercontent.com/18516460/69900331-063bc000-1372-11ea-84fc-983085d86844.png)
