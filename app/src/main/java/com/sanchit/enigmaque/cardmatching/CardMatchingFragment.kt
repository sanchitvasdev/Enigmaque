package com.sanchit.enigmaque.cardmatching

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.imageview.ShapeableImageView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.sanchit.enigmaque.R
import com.sanchit.enigmaque.databinding.FragmentCardMatchingBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import nl.dionsegijn.konfetti.models.Shape
import nl.dionsegijn.konfetti.models.Size
import java.lang.reflect.Type

class CardMatchingFragment : Fragment() {
    private lateinit var binding: FragmentCardMatchingBinding
    private lateinit var cardMatchingViewModel: CardMatchingViewModel
    private lateinit var setOfImages: HashMap<Int,Int>
    private lateinit var mapOfVisibilities: HashMap<Int,Int>
    private lateinit var map: HashMap<Int,Int>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        (activity as AppCompatActivity).supportActionBar?.title = "Card Matching"
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_card_matching, container, false)
        val application = requireNotNull(this.activity).application
        val cardMatchingViewModelFactory = CardMatchingViewModelFactory(application)
        cardMatchingViewModel = ViewModelProvider(this, cardMatchingViewModelFactory).get(CardMatchingViewModel::class.java)

        binding.lifecycleOwner = this
        binding.viewModel = cardMatchingViewModel

        val arrayOfCards = arrayOf(binding.card1, binding.card2, binding.card3,
                binding.card4, binding.card5, binding.card6, binding.card7, binding.card8, binding.card9,
                binding.card10, binding.card11, binding.card12, binding.card13, binding.card14, binding.card15,
                binding.card16, binding.card17, binding.card18, binding.card19, binding.card20)

        val listOfCards = mutableListOf<ShapeableImageView>()
        listOfCards.addAll(arrayOfCards.toList())

        val array = intArrayOf(R.mipmap.youtube_card_foreground, R.mipmap.gmail_card_foreground,
                R.mipmap.drive_card_foreground, R.mipmap.photos_card_foreground,
                R.mipmap.google_keep_card_foreground, R.mipmap.google_playstore_card_foreground,
                R.mipmap.chrome_card_foreground, R.mipmap.google_files_card_foreground,
                R.mipmap.google_lens_card_foreground, R.mipmap.google_maps_card_foreground,
                R.mipmap.youtube_card_foreground, R.mipmap.gmail_card_foreground,
                R.mipmap.drive_card_foreground, R.mipmap.photos_card_foreground,
                R.mipmap.google_keep_card_foreground, R.mipmap.google_playstore_card_foreground,
                R.mipmap.chrome_card_foreground, R.mipmap.google_files_card_foreground,
                R.mipmap.google_lens_card_foreground, R.mipmap.google_maps_card_foreground)

        val list_images = mutableListOf<Int>()
        list_images.addAll(array.toList())
        map = hashMapOf()

        val arrayOfInitialCardPictures = arrayOf(R.mipmap.back_card_pic_foreground,
                R.mipmap.back_card_pic_foreground,R.mipmap.back_card_pic_foreground,
                R.mipmap.back_card_pic_foreground,R.mipmap.back_card_pic_foreground,
                R.mipmap.back_card_pic_foreground,R.mipmap.back_card_pic_foreground,
                R.mipmap.back_card_pic_foreground,R.mipmap.back_card_pic_foreground,
                R.mipmap.back_card_pic_foreground,R.mipmap.back_card_pic_foreground,
                R.mipmap.back_card_pic_foreground,R.mipmap.back_card_pic_foreground,
                R.mipmap.back_card_pic_foreground,R.mipmap.back_card_pic_foreground,
                R.mipmap.back_card_pic_foreground,R.mipmap.back_card_pic_foreground,
                R.mipmap.back_card_pic_foreground,R.mipmap.back_card_pic_foreground,
                R.mipmap.back_card_pic_foreground)
        val listOfInitialCardPictures = mutableListOf<Int>()
        listOfInitialCardPictures.addAll(arrayOfInitialCardPictures.toList())
        setOfImages = hashMapOf()
        for (i in 1..20) {
            setOfImages[i] = listOfInitialCardPictures[i - 1]
        }

        val arrayOfVisibility = arrayOf(View.VISIBLE,View.VISIBLE,View.VISIBLE,View.VISIBLE,
                View.VISIBLE,View.VISIBLE,View.VISIBLE,View.VISIBLE,View.VISIBLE,View.VISIBLE,View.VISIBLE,
                View.VISIBLE,View.VISIBLE,View.VISIBLE,View.VISIBLE,View.VISIBLE,View.VISIBLE,View.VISIBLE,
                View.VISIBLE,View.VISIBLE)
        val listOfVisibility = mutableListOf<Int>()
        listOfVisibility.addAll(arrayOfVisibility.toList())
        mapOfVisibilities = hashMapOf()
        for (i in 1..20) {
            mapOfVisibilities[i] = listOfVisibility[i-1]
        }

        val sharedPreferences = application.getSharedPreferences("cardSituation", Context.MODE_PRIVATE)
        val score = sharedPreferences.getInt("score", 0)
        val jsonString2 = sharedPreferences.getString("map2","")
        val jsonString3 = sharedPreferences.getString("map3","")
        val jsonString4 = sharedPreferences.getString("map4","")
        if(jsonString2==""){
        }else{
            setOfImages = Gson().fromJson(jsonString2, object : TypeToken<HashMap<Int, Int>>() {}.type)
        }
        if(jsonString3==""){
        }else{
            mapOfVisibilities = Gson().fromJson(jsonString3, object : TypeToken<HashMap<Int, Int>>() {}.type)
        }
        if(jsonString4==""){
        }else{
            map = Gson().fromJson(jsonString4, object : TypeToken<HashMap<Int, Int>>() {}.type)
        }

        if(score<10){
            binding.tapCardTextView.visibility = View.VISIBLE
            binding.scoreTextView.visibility = View.VISIBLE
            binding.congratsTextView.visibility = View.GONE
            binding.konfettiView.visibility = View.INVISIBLE
            binding.resetBtn.visibility = View.GONE
            binding.card1.setImageResource(list_images[0])
            binding.card1.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.white, null))
        }
        if (score == 0) {
            binding.konfettiView.stopGracefully()
            cardMatchingViewModel.reset()
            for (i in 1..20) {
                val temp = listOfCards[i - 1]
                temp.isEnabled = true
                temp.visibility = View.VISIBLE
                if (i != 1) {
                    temp.setImageResource(R.mipmap.back_card_pic_foreground)
                }
            }
            list_images.shuffle()
            for (i in 1..20) {
                map[i] = list_images[i - 1]
            }
            for (i in 1..20) {
                mapOfVisibilities[i] = listOfVisibility[i-1]
            }
            for (i in 1..20) {
                setOfImages[i] = listOfInitialCardPictures[i - 1]
            }
            cardMatchingViewModel.check(map[1]!!, 1)
        }else if(score<10){
            for (i in 1..20) {
                val temp = listOfCards[i - 1]
                temp.isEnabled = true
                temp.visibility = mapOfVisibilities[i]!!
                if(i!=1){
                    if(setOfImages[i]!=R.mipmap.back_card_pic_foreground){
                        temp.setImageResource(setOfImages[i]!!)
                        temp.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.white, null))
                    }else{
                        temp.setImageResource(setOfImages[i]!!)
                    }
                }
            }
        }

        cardMatchingViewModel.score.observe(viewLifecycleOwner, Observer {
            binding.scoreTextView.text = resources.getString(R.string.yourscore, it)
            if (it == 10) {
                binding.tapCardTextView.visibility = View.GONE
                binding.scoreTextView.visibility = View.GONE
                binding.congratsTextView.visibility = View.VISIBLE
                binding.konfettiView.visibility = View.VISIBLE
                binding.resetBtn.visibility = View.VISIBLE
                for (i in 1..20) {
                    listOfCards[i - 1].visibility = View.INVISIBLE
                }
                for (i in 1..20) {
                    setOfImages[i] = listOfInitialCardPictures[i - 1]
                }
                for (i in 1..20) {
                    mapOfVisibilities[i] = listOfVisibility[i-1]
                }
                binding.konfettiView.build()
                        .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA, Color.BLUE, Color.WHITE, Color.CYAN)
                        .setDirection(0.0, 359.0)
                        .setSpeed(1f, 5f)
                        .setFadeOutEnabled(true)
                        .setTimeToLive(2000L)
                        .addShapes(Shape.Square, Shape.Circle)
                        .addSizes(Size(12))
                        .setPosition(-50f, binding.konfettiView.width + 50f, -50f, -50f)
                        .streamFor(300, 3000L)
                binding.resetBtn.setOnClickListener {
                    cardMatchingViewModel.reset()
                    for (i in 1..20) {
                        val temp = listOfCards[i - 1]
                        temp.isEnabled = true
                        temp.visibility = View.VISIBLE
                        if (i != 1) {
                            temp.setImageResource(R.mipmap.back_card_pic_foreground)
                        }
                    }
                    binding.tapCardTextView.visibility = View.VISIBLE
                    binding.scoreTextView.visibility = View.VISIBLE
                    binding.congratsTextView.visibility = View.GONE
                    binding.konfettiView.visibility = View.INVISIBLE
                    binding.resetBtn.visibility = View.GONE
                    list_images.shuffle()
                    for (i in 1..20) {
                        map[i] = list_images[i - 1]
                    }
                    for (i in 1..20) {
                        mapOfVisibilities[i] = listOfVisibility[i-1]
                    }
                    for (i in 1..20) {
                        setOfImages[i] = listOfInitialCardPictures[i - 1]
                    }
                    binding.card1.setImageResource(map[1]!!)
                    binding.card1.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.white, null))
                    cardMatchingViewModel.check(map[1]!!, 1)
                }
            }
        })

        for (i in 2..20) {
            val temp = listOfCards[i - 1]
            temp.setOnClickListener {
                animate(temp, i, map)
                temp.isEnabled = false
                setOfImages[i] = map[i]!!
            }
        }
        return binding.root
    }

    override fun onResume() {
        (activity as AppCompatActivity).supportActionBar?.title = "Card Matching"
        super.onResume()
    }

    private fun animate(card: ShapeableImageView, i: Int, map: HashMap<Int, Int>) {
        val oa1 = ObjectAnimator.ofFloat(card, "scaleX", 1f, 0f)
        val oa2 = ObjectAnimator.ofFloat(card, "scaleX", 0f, 0.9f)
        oa1.interpolator = DecelerateInterpolator()
        oa2.interpolator = AccelerateDecelerateInterpolator()
        oa1.duration = 150
        oa2.duration = 150
        oa1.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                card.setImageResource(map[i]!!)
                card.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.white, null))
                oa2.start()
            }
        })
        oa2.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                matched(cardMatchingViewModel.check(map[i]!!, i))
            }
        })
        oa1.start()
    }

    private fun matched(list: ArrayList<Int>) {
        if (list.size == 1) {
            return
        }
        GlobalScope.launch(Dispatchers.IO) {
            waitsomeTime()
        }
        idcheck(list[0])
        idcheck(list[1])
    }

    private fun waitsomeTime() {
        val start = System.currentTimeMillis()
        while (System.currentTimeMillis() < start + 1000);
    }

    private fun idcheck(id: Int) {
        mapOfVisibilities[id] = View.INVISIBLE
        when (id) {
            1 -> binding.card1.visibility = View.INVISIBLE
            2 -> binding.card2.visibility = View.INVISIBLE
            3 -> binding.card3.visibility = View.INVISIBLE
            4 -> binding.card4.visibility = View.INVISIBLE
            5 -> binding.card5.visibility = View.INVISIBLE
            6 -> binding.card6.visibility = View.INVISIBLE
            7 -> binding.card7.visibility = View.INVISIBLE
            8 -> binding.card8.visibility = View.INVISIBLE
            9 -> binding.card9.visibility = View.INVISIBLE
            10 -> binding.card10.visibility = View.INVISIBLE
            11 -> binding.card11.visibility = View.INVISIBLE
            12 -> binding.card12.visibility = View.INVISIBLE
            13 -> binding.card13.visibility = View.INVISIBLE
            14 -> binding.card14.visibility = View.INVISIBLE
            15 -> binding.card15.visibility = View.INVISIBLE
            16 -> binding.card16.visibility = View.INVISIBLE
            17 -> binding.card17.visibility = View.INVISIBLE
            18 -> binding.card18.visibility = View.INVISIBLE
            19 -> binding.card19.visibility = View.INVISIBLE
            20 -> binding.card20.visibility = View.INVISIBLE
        }
    }

    override fun onPause() {
        super.onPause()
        val application = requireNotNull(this.activity).application
        val sharedPreferences = application.getSharedPreferences("cardSituation", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("score", cardMatchingViewModel.score.value!!)

        val builder = GsonBuilder()
        val gson: Gson = builder.enableComplexMapKeySerialization().setPrettyPrinting().create()
        val type: Type = object : TypeToken<HashMap<Int, ArrayList<Int>>>() {}.type
        val json: String = gson.toJson(cardMatchingViewModel.mapCount, type)
        val type2: Type = object : TypeToken<HashMap<Int,Int>>(){}.type
        val json2 = gson.toJson(setOfImages,type2)
        val json3 = gson.toJson(mapOfVisibilities,type2)
        val json4 = gson.toJson(map,type2)
        editor.putString("map",json)
        editor.putString("map2",json2)
        editor.putString("map3",json3)
        editor.putString("map4",json4)
        editor.apply()
    }
}