using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class PlayerMiniGAme : MonoBehaviour
{
    public ControlType controlType;
    public Joystick joystick;
    public float speed;
    public float health;
    public int numOfHearts;
    public Image[] hearts;
    public Sprite fullHeart;
    public Sprite emptyHeart;
    public float heal;


    public enum ControlType{PC, Android}

    private Rigidbody2D rb;
    private Vector2 moveInput;
    private Vector2 moveVelocity;
    private Animator anim;

    private bool collision = false;
    private Collider2D collision_obj;
    private bool facingRight = true;

    private void Start()
    {

        rb = GetComponent<Rigidbody2D>();
        anim = GetComponent<Animator>();
        if (controlType == ControlType.PC)
        {
            joystick.gameObject.SetActive(false);
        }

    }



    private void Update()
    {

        if(controlType == ControlType.PC)
        {
            moveInput = new Vector2(Input.GetAxisRaw("Horizontal"), Input.GetAxisRaw("Vertical"));
        }
        else if(controlType == ControlType.Android)
        {
            moveInput = new Vector2(joystick.Horizontal, joystick.Vertical);
        }

        if (collision &&
            ((transform.position.x > collision_obj.transform.x && moveInput.x < 0)
                || (transform.position.x < collision_obj.transform.x && moveInput.x > 0)))
            return;

        moveVelocity = moveInput.normalized * speed;
        anim.SetBool("Run", (moveInput.x != 0));

        if((!facingRight && moveInput.x > 0) || (facingRight && moveInput.x < 0))
        {
            Flip();
        }

    }



    private void FixedUpdate()
    {
        if(health > numOfHearts)
        {
            health = numOfHearts;
        }

        health += Time.deltaTime * heal;
        for(int i = 0; i < hearts.Length; i++)
        {
            if(i < Mathf.RoundToInt(health))
            {
                hearts[i].sprite = fullHeart;
            }
            else
            {
                hearts[i].sprite = emptyHeart;
            }

            hearts[i].enabled = (i < numOfHearts);
        }
        if (!collision &&
            ((transform.position.x > collision_obj.transform.x && moveInput.x > 0)
                || (transform.position.x < collision_obj.transform.x && moveInput.x < 0)))
            rb.MovePosition(rb.position + moveVelocity * Time.fixedDeltaTime);
    }

    void Flip()
    {
        facingRight = !facingRight;
        Vector3 Scaler = transform.localScale;
        Scaler.x *= -1;
        transform.localScale = Scaler;
    }


    public void OnTriggerEnter2D(Collider2D otherCollider)
    {
        collision = true;
        collision_obj = otherCollider;
    }


    public void OnTriggerExit2D(Collider2D otherCollider)
    {
        collision = false;
        collision_obj = otherCollider;
    }

}