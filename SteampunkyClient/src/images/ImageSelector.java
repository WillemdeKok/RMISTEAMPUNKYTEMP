/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package images;

import javafx.scene.image.Image;

/**
 *
 * @author Melanie
 */
public class ImageSelector
{
    public Image getImage(String[] object, int level)
    {
        Image image = null;
        String url = "";
        
        if (object[0].equals("4"))
        {
            url = "ballista.png";
        }
        else if (object[0].equals("1"))
        {
            url = "player.png";
        }
        else if (object[0].equals("2"))
        {
            if (object[1].equals("box"))
            {      
                url = "obstacle.png";
            }
            else if (object[1].equals("cube"))
            {
                switch (level)
                {
                    case 1:
                        url = "box01.png";
                        break;
                    case 2:
                        url = "box02.png";
                        break;
                    case 3:
                        url = "box03.png";
                        break;
                }
            }
        }
        else if (object[0].equals("3"))
        {            
            switch (object[1])
            {
                case "ballista":
                    url = "powerup01.png";
                    break;
                case "torch":
                    url = "powerup02.png";
                    break;
                case "projectile":
                    url = "powerup05.png";
                    break;
            }
        }else if (object[0].equals("4"))
        {
            url = "ballista.png";
        }
        else if (object[0].equals("5"))
        {
            url = "arrow.png";
        }
        
        image = new Image(this.getClass().getResourceAsStream(url));
        return image;
    }
}
