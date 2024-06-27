package com.example.hw1

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.SwitchPreferenceCompat
import com.example.hw1.details.WeatherDetailsFragment

class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var metricPreference: SwitchPreferenceCompat
    private lateinit var animationPreference: SwitchPreferenceCompat

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        metricPreference = findPreference("unit")!!
        animationPreference = findPreference("animations")!!

        animationPreference.setOnPreferenceChangeListener { _, newValue ->
            val isChecked = newValue as Boolean
            animationPreference.title = if (isChecked) "On" else "Off"
            saveAnimationPreference(isChecked)
            true
        }

        metricPreference.setOnPreferenceChangeListener { _, newValue ->
            val isChecked = newValue as Boolean
            metricPreference.title = if (isChecked) "Imperial" else "Metric"
            saveMetricPreference(isChecked)
            true
        }
        restorePreferences()
    }

    private fun saveMetricPreference(isChecked: Boolean) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val editor = preferences.edit()
        val newTitle = if (isChecked) "Imperial" else "Metric"
        editor.putString("unit_preference_title", newTitle)
        editor.apply()
    }

    private fun saveAnimationPreference(isChecked: Boolean) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val editor = preferences.edit()
        val newTitle = if (isChecked) "On" else "Off"
        editor.putString("animation_preference_title", newTitle)
        editor.apply()
    }

    private fun restorePreferences() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val savedMetricTitle = preferences.getString("unit_preference_title", "Metric")
        val savedAnimationTitle = preferences.getString("animation_preference_title", "Off")
        metricPreference.title = savedMetricTitle
        animationPreference.title = savedAnimationTitle
    }
}
