# KMine

A Minecraft clone made in Kotlin

## Development process

### Day 1

This project is my first contact ever with LibGDX and LWJGL, I always made use of a Game engine or framework like SFML or Allegro.
Started by trying to display a simple cube, was straight forward using the already defined Box model in LibGDX; But for performance reasons, this wouldn't be enough for the project.
Knowing that, started experimenting with creating a cube by its vertices and mapping them to a single material and texture, differentiating only on the texture coordinates for each face

![Day 1](./.github/day1_0.png)

---

### Day 2

Created a simple Camera controller;
Added a turquoise background color to recall the sky;
Using the incredible project [OpenSimplexNoise](https://gist.github.com/KdotJPG/b1270127455a94ac5d19) (A following work post-perlin noise) to generate a simple terrain
with a simple chunk system, the chunks for now doesn't know its neighbors, therefore rendering unnecessary chunk borders (Todo this)

![Day 2 - 0](./.github/day2_0.png)

For the rest of the second day I fought with Gradle and IntelliJ, out of the blue the project stopped building
The wonderful Kotlin-ready-template made by [maltaisn](https://github.com/maltaisn/kmine) helped to solve the issues
- Pros -> I updated the libraries
- Cons -> Lost a significant amount of hours in this

After all the fighting I changed the noise function to use the third dimension

![Day 2 - 1](./.github/day2_1.png)

---

### Day 3

First of all, had my introduction to OpenGL ES shaders.
I was hoping to create a fragment shader that would invert the screen texture for the crosshair texture. Just like the original game
But I ended up using OpenGL blending to achieve the effect



![OpenGL blending](./.github/opengl_blending.png)

Started experimenting with lighting as well, trying to make a day-night cycle
And added the "torch cube" lol
![Day 3 - 0](./.github/day3_0.png)